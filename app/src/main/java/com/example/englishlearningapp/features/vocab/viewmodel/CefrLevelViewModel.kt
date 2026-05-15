package com.example.englishlearningapp.features.vocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.VocabApiService
import com.example.englishlearningapp.data.remote.api.response.TopicResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

// Represents one topic card in the CEFR level screen
data class TopicProgressItem(
    val topic: TopicResponse,
    val totalWords: Int,
    val learnedCount: Int = 0,
    val reviewCount: Int = 0
)

class CefrLevelViewModel(context: Context) : ViewModel() {

    private val vocabApiService: VocabApiService = RetrofitClient.vocabApiService

    private val _topicItems = MutableStateFlow<List<TopicProgressItem>>(emptyList())
    val topicItems: StateFlow<List<TopicProgressItem>> = _topicItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val totalWords: StateFlow<Int> = _topicItems
        .map { items -> items.sumOf { it.totalWords } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalLearned: StateFlow<Int> = _topicItems
        .map { items -> items.sumOf { it.learnedCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun loadLevel(level: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Fetch vocabs and topics in parallel
                val vocabsDeferred = async { vocabApiService.getAllVocabularies(level) }
                val topicsDeferred = async { vocabApiService.getTopics() }
                val learnedVocabsDeferred = async { vocabApiService.getLearnedVocabs() }

                val vocabs = vocabsDeferred.await()
                val allTopics = topicsDeferred.await()
                val learnedVocabIds = learnedVocabsDeferred.await().items
                    .map { it.vocabularyId }
                    .toSet()

                val countByTopicId: Map<Int, Int> = vocabs
                    .groupBy { it.topicId }
                    .mapValues { it.value.size }

                val learnedCountByTopicId: Map<Int, Int> = vocabs
                    .filter { it.id in learnedVocabIds }
                    .groupBy { it.topicId }
                    .mapValues { it.value.size }

                val levelTopics = allTopics.filter { topic ->
                    topic.level == level && countByTopicId.containsKey(topic.id)
                }

                _topicItems.value = levelTopics
                    .map { topic ->
                        TopicProgressItem(
                            topic = topic,
                            totalWords = countByTopicId[topic.id] ?: 0,
                            learnedCount = learnedCountByTopicId[topic.id] ?: 0
                        )
                    }
                    .sortedBy { it.topic.name }

            } catch (e: Exception) {
                _error.value = "Không thể tải dữ liệu. Vui lòng thử lại."
            } finally {
                _isLoading.value = false
            }
        }
    }


}

