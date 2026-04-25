package com.example.englishlearningapp.features.vocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import com.example.englishlearningapp.data.repository.VocabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class VocabViewModel @Inject constructor(
    private val repository: VocabRepository
) : ViewModel() {

    private val userId = 1
    private val _searchQuery = MutableStateFlow("")
    private val _selectedTopicId = MutableStateFlow<Int?>(null)
    private val _difficultyFilter = MutableStateFlow<String?>(null)
    private val _vocabCountByLevel = MutableStateFlow<Map<String, Int>>(emptyMap())

    val topics: StateFlow<List<TopicWithCount>> = repository.getTopicsWithWordCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val vocabCountByLevel: StateFlow<Map<String, Int>> = _vocabCountByLevel.asStateFlow()

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val difficultyFilter: StateFlow<String?> = _difficultyFilter.asStateFlow()

    val searchResults: StateFlow<List<VocabularyEntity>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.length < 2) {
                flowOf(emptyList())
            } else {
                repository.searchVocabs(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val currentTopicVocabs: StateFlow<List<VocabularyEntity>> = combine(
        _selectedTopicId,
        _difficultyFilter
    ) { topicId, difficulty -> topicId to difficulty }
        .flatMapLatest { (topicId, difficulty) ->
            if (topicId == null) {
                flowOf(emptyList())
            } else if (difficulty == null) {
                repository.getVocabsByTopic(topicId)
            } else {
                repository.getVocabsByTopicFiltered(topicId, difficulty)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val savedVocabs: StateFlow<List<VocabularyEntity>> = repository.getSavedVocabs(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadVocabCountByLevel()
    }

    fun loadTopics() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_selectedTopicId.value == null) {
                repository.getTopicsWithWordCount().firstOrNull()?.firstOrNull()?.let { firstTopic ->
                    _selectedTopicId.value = firstTopic.topic.id
                }
            }
        }
    }

    private fun loadVocabCountByLevel() {
        viewModelScope.launch {
            repository.getVocabCountByLevel().collect { countMap ->
                _vocabCountByLevel.value = countMap
            }
        }
    }

    fun selectTopic(topicId: Int) {
        _selectedTopicId.value = topicId
    }

    fun setDifficultyFilter(difficulty: String?) {
        _difficultyFilter.value = difficulty
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun toggleSave(vocabId: Int, currentlySaved: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleSave(userId, vocabId, !currentlySaved)
        }
    }
}

