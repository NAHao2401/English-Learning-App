package com.example.englishlearningapp.features.vocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.local.db.AppDatabase
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import com.example.englishlearningapp.data.local.db.entity.UserEntity
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import com.example.englishlearningapp.data.repository.VocabRepository
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class VocabViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val repository: VocabRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val appDataStore = AppDataStore(context)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedTopicId = MutableStateFlow<Int?>(null)
    private val _difficultyFilter = MutableStateFlow<String?>(null)
    private val _vocabCountByLevel = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    private val _currentLevelVocabs = MutableStateFlow<List<VocabularyEntity>>(emptyList())
    private val _levelVocabularyError = MutableStateFlow<String?>(null)
    private val _isLoadingLevelVocabs = MutableStateFlow(false)

    val topics: StateFlow<List<TopicWithCount>> = repository.getTopicsWithWordCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val vocabCountByLevel: StateFlow<Map<String, Int>> = _vocabCountByLevel.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val currentLevelVocabs: StateFlow<List<VocabularyEntity>> = _currentLevelVocabs.asStateFlow()

    val levelVocabularyError: StateFlow<String?> = _levelVocabularyError.asStateFlow()

    val isLoadingLevelVocabs: StateFlow<Boolean> = _isLoadingLevelVocabs.asStateFlow()

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

    val savedVocabs: StateFlow<List<VocabularyEntity>> =
        appDataStore.userId
            .flatMapLatest { id -> repository.getSavedVocabs(id) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Review quiz vocabs pool
    private val _reviewVocabs = MutableStateFlow<List<VocabularyEntity>>(emptyList())
    val reviewVocabs: StateFlow<List<VocabularyEntity>> = _reviewVocabs.asStateFlow()

    // All vocabs pool for generating wrong answer options
    private val _allVocabsPool = MutableStateFlow<List<VocabularyEntity>>(emptyList())

    init {
        loadVocabCountByLevel()
        loadCurrentUser()
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

    private fun loadCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uid = appDataStore.userId.first()
                _currentUser.value = appDatabase.userDao().getUserById(uid)
            } catch (_: Exception) {
                _currentUser.value = null
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

    fun loadVocabsByLevel(level: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingLevelVocabs.value = true
            _levelVocabularyError.value = null
            try {
                val result = repository.getVocabsByLevel(level)
                _currentLevelVocabs.value = result.getOrElse {
                    _levelVocabularyError.value = it.message ?: "Không thể tải từ vựng theo level"
                    emptyList()
                }
            } finally {
                _isLoadingLevelVocabs.value = false
            }
        }
    }

    fun toggleSave(vocabId: Int, currentlySaved: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uid = appDataStore.userId.first()
                repository.toggleSave(uid, vocabId, !currentlySaved)
            } catch (_: Exception) {
                // ignore errors to avoid crashes from DB/network issues
            }
        }
    }

    fun setSaved(vocabId: Int, saved: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uid = appDataStore.userId.first()
                repository.toggleSave(uid, vocabId, saved)
            } catch (_: Exception) {
                // ignore errors to avoid crashes from DB/network issues
            }
        }
    }

    // Prepare review quiz from saved words
    fun prepareReviewFromSaved() {
        _reviewVocabs.value = savedVocabs.value.shuffled()
    }

    // Prepare review quiz from topic
    fun prepareReviewFromTopic(topicId: Int) {
        viewModelScope.launch {
            try {
                val vocabs = repository.getVocabsByTopic(topicId).firstOrNull() ?: emptyList()
                _reviewVocabs.value = vocabs.shuffled()
            } catch (e: Exception) {
                _reviewVocabs.value = emptyList()
            }
        }
    }

    // Ensure vocab pool is loaded for generating wrong options
    fun ensureVocabPool() {
        if (_allVocabsPool.value.isNotEmpty()) return
        viewModelScope.launch {
            try {
                val allVocabs = repository.getVocabCountByLevel().firstOrNull()
                    ?.let {
                        repository.getVocabsByTopic(1).firstOrNull() ?: emptyList<VocabularyEntity>()
                    }
                    ?: emptyList<VocabularyEntity>()
                _allVocabsPool.value = allVocabs
            } catch (_: Exception) {
                // silently fail, quiz will use available pool
            }
        }
    }

    // Get wrong answer options for quiz
    fun getWrongOptions(correctId: Int, count: Int = 3): List<String> {
        return _allVocabsPool.value
            .filter { it.id != correctId }
            .shuffled()
            .take(count)
            .map { it.meaning }
            .distinct()
            .take(count)
    }
}
