package com.example.englishlearningapp.features.usertopic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.VocabApiService
import com.example.englishlearningapp.data.remote.api.response.UserTopicCreateRequest
import com.example.englishlearningapp.data.remote.api.response.UserTopicResponse
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.data.remote.api.response.SaveVocabularyRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserTopicViewModel(context: Context) : ViewModel() {

    private val vocabApiService: VocabApiService = RetrofitClient.vocabApiService

    // --- User topics list ---
    private val _userTopics = MutableStateFlow<List<UserTopicResponse>>(emptyList())
    val userTopics: StateFlow<List<UserTopicResponse>> = _userTopics.asStateFlow()

    private val _topicWordCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val topicWordCounts: StateFlow<Map<Int, Int>> = _topicWordCounts.asStateFlow()

    private val _topicLearnedCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val topicLearnedCounts: StateFlow<Map<Int, Int>> = _topicLearnedCounts.asStateFlow()

    private val _isLoadingTopics = MutableStateFlow(false)
    val isLoadingTopics: StateFlow<Boolean> = _isLoadingTopics.asStateFlow()

    private val _topicsError = MutableStateFlow<String?>(null)
    val topicsError: StateFlow<String?> = _topicsError.asStateFlow()

    // --- Topic detail (vocab list) ---
    private val _topicVocabs = MutableStateFlow<List<VocabularyResponse>>(emptyList())
    val topicVocabs: StateFlow<List<VocabularyResponse>> = _topicVocabs.asStateFlow()

    private val _isLoadingVocabs = MutableStateFlow(false)
    val isLoadingVocabs: StateFlow<Boolean> = _isLoadingVocabs.asStateFlow()

    private val _vocabsError = MutableStateFlow<String?>(null)
    val vocabsError: StateFlow<String?> = _vocabsError.asStateFlow()

    // --- Create topic dialog state ---
    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()

    private val _createError = MutableStateFlow<String?>(null)
    val createError: StateFlow<String?> = _createError.asStateFlow()

    // --- Save vocabulary to topic state ---
    private val _saveSuccess = MutableStateFlow<String?>(null)
    val saveSuccess: StateFlow<String?> = _saveSuccess.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _removeSuccess = MutableStateFlow<String?>(null)
    val removeSuccess: StateFlow<String?> = _removeSuccess.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _savedVocabIds = MutableStateFlow<Set<Int>>(emptySet())
    val savedVocabIds: StateFlow<Set<Int>> = _savedVocabIds.asStateFlow()

    fun loadUserTopics() {
        viewModelScope.launch {
            val showLoading = _userTopics.value.isEmpty()
            if (showLoading) {
                _isLoadingTopics.value = true
            }
            _topicsError.value = null
            try {
                val fetched = vocabApiService.getUserTopics()
                val learnedIds = try {
                    vocabApiService.getLearnedVocabs().items.map { it.vocabularyId }.toSet()
                } catch (_: Exception) {
                    emptySet()
                }
                _userTopics.value = fetched

                val countPairs = fetched.map { topic ->
                    async {
                        try {
                            val vocabs = vocabApiService.getUserTopicVocabularies(topic.id)
                            val wordCount = vocabs.size
                            val learnedCount = vocabs.count { it.id in learnedIds }
                            Triple(topic.id, wordCount, learnedCount)
                        } catch (_: Exception) {
                            Triple(topic.id, 0, 0)
                        }
                    }
                }.awaitAll()

                _topicWordCounts.value = countPairs.associate { it.first to it.second }
                _topicLearnedCounts.value = countPairs.associate { it.first to it.third }
            } catch (e: Exception) {
                _topicsError.value = "Không thể tải thư mục. Vui lòng thử lại."
            } finally {
                if (showLoading) {
                    _isLoadingTopics.value = false
                }
            }
        }
    }

    fun refreshTopicWordCount(userTopicId: Int) {
        viewModelScope.launch {
            try {
                val vocabs = vocabApiService.getUserTopicVocabularies(userTopicId)
                _topicWordCounts.update { current -> current + (userTopicId to vocabs.size) }
                val learnedIds = try {
                    vocabApiService.getLearnedVocabs().items.map { it.vocabularyId }.toSet()
                } catch (_: Exception) {
                    emptySet()
                }
                _topicLearnedCounts.update { current -> current + (userTopicId to vocabs.count { it.id in learnedIds }) }
            } catch (_: Exception) {
                // keep existing value if refresh fails
            }
        }
    }

    fun loadTopicVocabs(userTopicId: Int) {
        viewModelScope.launch {
            _isLoadingVocabs.value = true
            _vocabsError.value = null
            _topicVocabs.value = emptyList()
            try {
                _topicVocabs.value = vocabApiService.getUserTopicVocabularies(userTopicId)
            } catch (e: Exception) {
                _vocabsError.value = "Không thể tải từ vựng. Vui lòng thử lại."
            } finally {
                _isLoadingVocabs.value = false
            }
        }
    }

    fun removeVocabularyFromTopic(userTopicId: Int, vocabularyId: Int) {
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            _removeSuccess.value = null
            try {
                vocabApiService.deleteUserTopicVocabulary(userTopicId, vocabularyId)
                _savedVocabIds.value = _savedVocabIds.value - vocabularyId
                loadTopicVocabs(userTopicId)
                refreshTopicWordCount(userTopicId)
                loadUserTopics()
                _removeSuccess.value = "Đã xóa từ khỏi thư mục"
            } catch (e: HttpException) {
                _saveError.value = when (e.code()) {
                    404 -> "Không tìm thấy thư mục hoặc từ trong thư mục"
                    else -> "Xóa thất bại. Vui lòng thử lại."
                }
            } catch (e: Exception) {
                _saveError.value = "Xóa thất bại. Vui lòng thử lại."
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun showCreateDialog() { _showCreateDialog.value = true }
    fun hideCreateDialog() { _showCreateDialog.value = false; _createError.value = null }

    fun createUserTopic(name: String, description: String?) {
        if (name.isBlank()) {
            _createError.value = "Tên thư mục không được để trống"
            return
        }
        viewModelScope.launch {
            _isCreating.value = true
            _createError.value = null
            try {
                val newTopic = vocabApiService.createUserTopic(
                    UserTopicCreateRequest(name.trim(), description?.trim())
                )
                _userTopics.value = _userTopics.value + newTopic
                _showCreateDialog.value = false
            } catch (e: Exception) {
                _createError.value = "Tạo thư mục thất bại. Vui lòng thử lại."
            } finally {
                _isCreating.value = false
            }
        }
    }

    fun getTopicById(id: Int): UserTopicResponse? = _userTopics.value.find { it.id == id }

    fun saveVocabularyToTopic(vocabularyId: Int, userTopicId: Int) {
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            try {
                vocabApiService.saveVocabulary(
                    SaveVocabularyRequest(
                        vocabularyId = vocabularyId,
                        userTopicId = userTopicId
                    )
                )
                _savedVocabIds.value = _savedVocabIds.value + vocabularyId
                _saveSuccess.value = "Đã lưu từ vựng thành công!"
            } catch (e: HttpException) {
                if (e.code() == 400) {
                    _saveError.value = "Từ này đã có trong thư mục rồi!"
                } else {
                    _saveError.value = "Lưu thất bại. Vui lòng thử lại."
                }
            } catch (e: Exception) {
                _saveError.value = "Lưu thất bại. Vui lòng thử lại."
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearSaveFeedback() {
        _saveSuccess.value = null
        _saveError.value = null
    }

    fun clearRemoveFeedback() {
        _removeSuccess.value = null
    }

    fun createTopicAndSave(topicName: String, vocabularyId: Int) {
        viewModelScope.launch {
            _isCreating.value = true
            _saveError.value = null
            try {
                val newTopic = vocabApiService.createUserTopic(
                    UserTopicCreateRequest(name = topicName.trim(), description = null)
                )
                // Add to local list immediately (optimistic update)
                _userTopics.value = _userTopics.value + newTopic
                // Then save vocab to new topic
                saveVocabularyToTopic(vocabularyId, newTopic.id)
            } catch (e: Exception) {
                _saveError.value = "Tạo thư mục thất bại. Vui lòng thử lại."
            } finally {
                _isCreating.value = false
            }
        }
    }


}




