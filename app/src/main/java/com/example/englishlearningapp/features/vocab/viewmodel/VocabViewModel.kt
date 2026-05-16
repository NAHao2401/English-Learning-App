package com.example.englishlearningapp.features.vocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.local.db.DatabaseProvider
import com.example.englishlearningapp.data.local.db.AppDatabase
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import com.example.englishlearningapp.data.local.db.entity.UserEntity
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import com.example.englishlearningapp.data.repository.VocabRepository
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.VocabApiService
import com.example.englishlearningapp.data.remote.api.response.RateVocabRequest
import com.example.englishlearningapp.data.remote.api.response.TopicStudyResponse
import com.example.englishlearningapp.data.remote.api.response.UserVocabularyResponse
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.data.remote.api.response.VocabOverviewResponse
import com.example.englishlearningapp.data.remote.api.response.LearnedVocabListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VocabViewModel(context: Context) : ViewModel() {

    private val appContext = context.applicationContext
    private val appDatabase: AppDatabase = DatabaseProvider.getDatabase(appContext)
    private val repository = VocabRepository(appContext)
    private val vocabApiService: VocabApiService = RetrofitClient.vocabApiService
    private val appDataStore = AppDataStore(appContext)
    private val _searchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<VocabularyResponse>>(emptyList())
    private val _isSearching = MutableStateFlow(false)
    private val _searchProgress = MutableStateFlow<Map<Int, UserVocabularyResponse>>(emptyMap())
    private var searchJob: Job? = null
    private val _selectedTopicId = MutableStateFlow<Int?>(null)
    private val _difficultyFilter = MutableStateFlow<String?>(null)
    private val _vocabCountByLevel = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    private val _currentLevelVocabs = MutableStateFlow<List<VocabularyEntity>>(emptyList())
    private val _levelVocabularyError = MutableStateFlow<String?>(null)
    private val _isLoadingLevelVocabs = MutableStateFlow(false)
    private val _topicVocabs = MutableStateFlow<List<VocabularyResponse>>(emptyList())
    private val _topicVocabError = MutableStateFlow<String?>(null)
    private val _isLoadingTopicVocabs = MutableStateFlow(false)
    private val _studySession = MutableStateFlow<TopicStudyResponse?>(null)
    val studySession: StateFlow<TopicStudyResponse?> = _studySession.asStateFlow()

    private val _topicProgress = MutableStateFlow<Map<Int, UserVocabularyResponse>>(emptyMap())
    val topicProgress: StateFlow<Map<Int, UserVocabularyResponse>> = _topicProgress.asStateFlow()

    private val _isRating = MutableStateFlow(false)
    val isRating: StateFlow<Boolean> = _isRating.asStateFlow()

    private val _vocabOverview = MutableStateFlow<VocabOverviewResponse?>(null)
    val vocabOverview: StateFlow<VocabOverviewResponse?> = _vocabOverview.asStateFlow()

    private val _isLoadingOverview = MutableStateFlow(false)
    val isLoadingOverview: StateFlow<Boolean> = _isLoadingOverview.asStateFlow()

    private val _learnedVocabs = MutableStateFlow<LearnedVocabListResponse?>(null)
    val learnedVocabs: StateFlow<LearnedVocabListResponse?> = _learnedVocabs.asStateFlow()

    private val _learnedCountByLevel = MutableStateFlow<Map<String, Int>>(emptyMap())
    val learnedCountByLevel: StateFlow<Map<String, Int>> = _learnedCountByLevel.asStateFlow()

    private val _topicLearnedCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val topicLearnedCounts: StateFlow<Map<Int, Int>> = _topicLearnedCounts.asStateFlow()

    private val _isLoadingLearned = MutableStateFlow(false)
    val isLoadingLearned: StateFlow<Boolean> = _isLoadingLearned.asStateFlow()

    val topics: StateFlow<List<TopicWithCount>> = repository.getTopicsWithWordCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val vocabCountByLevel: StateFlow<Map<String, Int>> = _vocabCountByLevel.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val currentLevelVocabs: StateFlow<List<VocabularyEntity>> = _currentLevelVocabs.asStateFlow()

    val levelVocabularyError: StateFlow<String?> = _levelVocabularyError.asStateFlow()

    val isLoadingLevelVocabs: StateFlow<Boolean> = _isLoadingLevelVocabs.asStateFlow()

    val topicVocabs: StateFlow<List<VocabularyResponse>> = combine(
        _topicVocabs,
        _difficultyFilter
    ) { vocabs, difficulty ->
        if (difficulty == null) {
            vocabs
        } else {
            vocabs.filter { it.difficulty == difficulty }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val topicVocabError: StateFlow<String?> = _topicVocabError.asStateFlow()

    val isLoadingTopicVocabs: StateFlow<Boolean> = _isLoadingTopicVocabs.asStateFlow()

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<VocabularyResponse>> = _searchResults.asStateFlow()

    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    val searchProgress: StateFlow<Map<Int, UserVocabularyResponse>> = _searchProgress.asStateFlow()

    val difficultyFilter: StateFlow<String?> = _difficultyFilter.asStateFlow()

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

    // Free practice pool: all learned words sorted by mastery (from API)
    private val _freePracticeWords = MutableStateFlow<List<VocabularyResponse>>(emptyList())
    val freePracticeWords: StateFlow<List<VocabularyResponse>> = _freePracticeWords.asStateFlow()

    private val _isLoadingFreePractice = MutableStateFlow(false)
    val isLoadingFreePractice: StateFlow<Boolean> = _isLoadingFreePractice.asStateFlow()

    // Extra pool for wrong options in free practice
    private val _freePracticeDistractorPool = MutableStateFlow<List<VocabularyResponse>>(emptyList())
    val freePracticeDistractorPool: StateFlow<List<VocabularyResponse>> = _freePracticeDistractorPool.asStateFlow()

    init {
        loadVocabCountByLevel()
        loadCurrentUser()
        syncTopicsFromApi()
        loadVocabOverview()
        loadTopicLearnedCounts()
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

    private fun syncTopicsFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncTopicsFromApi()
        }
    }

    fun selectTopic(topicId: Int) {
        _selectedTopicId.value = topicId
    }

    fun loadTopicDetail(topicId: Int) {
        viewModelScope.launch {
            _isLoadingTopicVocabs.value = true
            _topicVocabError.value = null
            try {
                val vocabsDeferred = async { vocabApiService.getVocabulariesByTopic(topicId) }
                val progressDeferred = async {
                    try {
                        vocabApiService.getTopicProgress(topicId)
                    } catch (_: Exception) {
                        emptyMap()
                    }
                }
                val studyDeferred = async {
                    try {
                        vocabApiService.getStudySession(topicId)
                    } catch (_: Exception) {
                        null
                    }
                }

                _selectedTopicId.value = topicId
                _topicVocabs.value = vocabsDeferred.await()
                _topicProgress.value = progressDeferred.await()
                _studySession.value = studyDeferred.await()
            } catch (_: Exception) {
                _topicVocabError.value = "Không thể tải dữ liệu."
            } finally {
                _isLoadingTopicVocabs.value = false
            }
        }
    }

    fun loadStudySession(topicId: Int) {
        viewModelScope.launch {
            _isLoadingTopicVocabs.value = true
            _topicVocabError.value = null
            try {
                _studySession.value = null
                _selectedTopicId.value = topicId
                val progressDeferred = async {
                    try {
                        vocabApiService.getTopicProgress(topicId)
                    } catch (_: Exception) {
                        emptyMap()
                    }
                }
                val studyDeferred = async {
                    try {
                        vocabApiService.getStudySession(topicId)
                    } catch (_: Exception) {
                        null
                    }
                }
                _topicProgress.value = progressDeferred.await()
                _studySession.value = studyDeferred.await() ?: TopicStudyResponse(topicId = topicId)
            } catch (_: Exception) {
                _topicVocabError.value = "Không thể tải dữ liệu."
                _studySession.value = TopicStudyResponse(topicId = topicId)
            } finally {
                _isLoadingTopicVocabs.value = false
            }
        }
    }

    fun setDifficultyFilter(difficulty: String?) {
        _difficultyFilter.value = difficulty
    }

    fun updateSearch(query: String) {
        updateSearchQuery(query)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            searchJob?.cancel()
            _searchResults.value = emptyList()
            _searchProgress.value = emptyMap()
            _isSearching.value = false
            return
        }

        _isSearching.value = true
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            try {
                val prefix = query.trim()
                val results = vocabApiService.searchVocabularies(prefix)
                _searchResults.value = results

                if (results.isNotEmpty()) {
                    val ids = results.map { it.id }.joinToString(",")
                    _searchProgress.value = try {
                        vocabApiService.getBatchProgress(ids)
                    } catch (_: Exception) {
                        emptyMap()
                    }
                } else {
                    _searchProgress.value = emptyMap()
                }
            } catch (_: Exception) {
                _searchResults.value = emptyList()
                _searchProgress.value = emptyMap()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _searchProgress.value = emptyMap()
        _isSearching.value = false
        searchJob?.cancel()
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

    fun rateVocabulary(
        vocabularyId: Int,
        rating: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isRating.value = true
            try {
                val result = vocabApiService.rateVocabulary(
                    RateVocabRequest(vocabularyId, rating)
                )

                val updated = _topicProgress.value.toMutableMap()
                updated[vocabularyId] = result
                _topicProgress.value = updated

                val currentSession = _studySession.value
                if (currentSession != null) {
                    _studySession.value = currentSession.copy(
                        newWords = currentSession.newWords.filter { it.id != vocabularyId },
                        dueReviewWords = currentSession.dueReviewWords.filter { it.id != vocabularyId },
                        learnedCount = currentSession.learnedCount + 1
                    )
                }

                onSuccess()
            } catch (_: Exception) {
                onSuccess()
            } finally {
                _isRating.value = false
            }
        }
    }

    // Rate answer during quiz: adjust mastery up/down and send rating
    fun rateQuizAnswer(
        vocabularyId: Int,
        currentMastery: Int,
        isCorrect: Boolean,
        onDone: () -> Unit = {}
    ) {
        val newRating = if (isCorrect) {
            minOf(currentMastery + 1, 5)
        } else {
            maxOf(currentMastery - 1, 1)
        }

        viewModelScope.launch {
            try {
                vocabApiService.rateVocabulary(
                    RateVocabRequest(vocabularyId, newRating)
                )
            } catch (e: Exception) {
                // silently ignore
            } finally {
                onDone()
            }
        }
    }

    fun loadVocabOverview() {
        viewModelScope.launch {
            _isLoadingOverview.value = true
            try {
                _vocabOverview.value = vocabApiService.getVocabOverview()
            } catch (e: Exception) {
                // silently fail — show 0s
                _vocabOverview.value = VocabOverviewResponse()
            } finally {
                _isLoadingOverview.value = false
            }
        }
    }

    fun loadLearnedVocabs() {
        viewModelScope.launch {
            _isLoadingLearned.value = true
            try {
                // Fetch from API
                val resp = vocabApiService.getLearnedVocabs()
                val learnedIds = resp.items.map { it.vocabularyId }.toSet()

                // Enrich items with audioUrl from local DB or allVocabsPool when missing
                val enriched = if (resp.items.isNotEmpty()) {
                    try {
                        val localEntities = try {
                            repository.getVocabsByIds(learnedIds.toList()).firstOrNull() ?: emptyList()
                        } catch (_: Exception) {
                            emptyList()
                        }
                        val localMap = localEntities.associateBy { it.id }
                        val poolMap = _allVocabsPool.value.associateBy { it.id }
                        val topicLevelById = topics.value.associate { topic ->
                            topic.topic.id to (topic.topic.level?.uppercase() ?: "")
                        }

                        _learnedCountByLevel.value = localEntities
                            .mapNotNull { entity -> topicLevelById[entity.topicId]?.takeIf { it.isNotBlank() } }
                            .groupingBy { it }
                            .eachCount()

                        resp.items.map { item ->
                            val audioFromLocal = localMap[item.vocabularyId]?.audioUrl
                            val audioFromPool = poolMap[item.vocabularyId]?.audioUrl
                            val audio = item.audioUrl ?: audioFromLocal ?: audioFromPool
                            item.copy(audioUrl = audio)
                        }
                    } catch (_: Exception) {
                        _learnedCountByLevel.value = emptyMap()
                        resp.items
                    }
                } else resp.items

                if (resp.items.isEmpty()) {
                    _learnedCountByLevel.value = emptyMap()
                }

                _learnedVocabs.value = resp.copy(items = enriched)
                loadTopicLearnedCounts(learnedIds)
            } catch (e: Exception) {
                // handle error silently
                _learnedVocabs.value = LearnedVocabListResponse()
                _learnedCountByLevel.value = emptyMap()
                _topicLearnedCounts.value = emptyMap()
            } finally {
                _isLoadingLearned.value = false
            }
        }
    }

    fun loadFreePracticeWords() {
        viewModelScope.launch {
            _isLoadingFreePractice.value = true
            try {
                val learnedPoolDeferred = async { vocabApiService.getLearnedPracticePool() }
                val distractorPoolDeferred = async {
                    try {
                        vocabApiService.getAllVocabularies(null)
                    } catch (_: Exception) {
                        emptyList()
                    }
                }

                _freePracticeWords.value = learnedPoolDeferred.await()
                _freePracticeDistractorPool.value = distractorPoolDeferred.await()
            } catch (_: Exception) {
                _freePracticeWords.value = emptyList()
                _freePracticeDistractorPool.value = emptyList()
            } finally {
                _isLoadingFreePractice.value = false
            }
        }
    }

    private fun loadTopicLearnedCounts(learnedIds: Set<Int>? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val learnedSet = learnedIds ?: try {
                    vocabApiService.getLearnedVocabs().items.map { it.vocabularyId }.toSet()
                } catch (_: Exception) {
                    emptySet()
                }

                val localTopics = repository.getTopics().firstOrNull().orEmpty()
                val countsByTopic = localTopics.associate { topic ->
                    val localVocabs = repository.getVocabsByTopic(topic.id).firstOrNull().orEmpty()
                    topic.id to localVocabs.count { it.id in learnedSet }
                }
                _topicLearnedCounts.value = countsByTopic
            } catch (_: Exception) {
                _topicLearnedCounts.value = emptyMap()
            }
        }
    }

    val newWordCount: StateFlow<Int> = _studySession
        .map { it?.newWords?.size ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val hasMoreNewWords: StateFlow<Boolean> = _studySession
        .map { (it?.newWords?.size ?: 0) > 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val learnedCount: StateFlow<Int> = _studySession
        .map { it?.learnedCount ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val totalWords: StateFlow<Int> = _studySession
        .map { it?.totalWords ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val studyBatch: StateFlow<List<VocabularyResponse>> = _studySession
        .map { it?.newWords?.take(8) ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
        val pool = (_freePracticeWords.value + _freePracticeDistractorPool.value)
            .distinctBy { it.id }

        return pool
            .filter { it.id != correctId }
            .shuffled()
            .take(count)
            .map { it.meaning }
            .distinct()
            .take(count)
    }


}
