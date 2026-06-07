package com.example.englishlearningapp.features.speaking.viewmodel

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.local.db.dao.SpeakingPracticeDao
import com.example.englishlearningapp.data.local.db.entity.SpeakingPracticeEntity
<<<<<<< HEAD
import com.example.englishlearningapp.data.local.db.entity.UserEntity
import com.example.englishlearningapp.data.remote.api.SpeakingApiService
=======
import com.example.englishlearningapp.data.remote.api.SpeakingApiService
import com.example.englishlearningapp.data.remote.dto.SpeakingResultRequest
>>>>>>> a96e346 (fix ui + speaking)
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpeakingViewModel(
<<<<<<< HEAD
    context: Context,
    private val speakingPracticeDao: SpeakingPracticeDao,
    private val apiService: SpeakingApiService
=======
    private val context: Context,
    private val speakingDao: SpeakingPracticeDao,
    private val speakingApiService: SpeakingApiService,
    private val appDataStore: AppDataStore
>>>>>>> a96e346 (fix ui + speaking)
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeakingUiState())
    val uiState: StateFlow<SpeakingUiState> = _uiState.asStateFlow()

<<<<<<< HEAD
=======
    private var speechRecognizer: SpeechRecognizer? = null
    private var currentUserId: Int = -1

    init {
        // Đọc userId từ DataStore khi ViewModel khởi tạo
        viewModelScope.launch {
            currentUserId = appDataStore.userId.first()
        }
    }

>>>>>>> a96e346 (fix ui + speaking)
    fun loadTopics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
<<<<<<< HEAD
                val topics = apiService.getSpeakingTopics()
                _uiState.update { it.copy(topics = topics, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load topics: ${e.message}"
                    )
                }
=======
                val topics = speakingApiService.getSpeakingTopics()
                _uiState.update {
                    it.copy(
                        topics = topics,
                        selectedTopic = null,
                        sentences = emptyList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Could not load topics")
                }
>>>>>>> a96e346 (fix ui + speaking)
            }
        }
    }

<<<<<<< HEAD
    fun selectTopic(topic: com.example.englishlearningapp.data.remote.dto.SpeakingTopicDto) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedTopic = topic,
                    isLoading = true,
                    errorMessage = null,
                    currentIndex = 0
                )
            }
            try {
                val sentences = apiService.getSpeakingSentences(topic.id)
                _uiState.update {
                    it.copy(
                        sentences = sentences,
                        currentSentence = sentences.firstOrNull(),
                        isLoading = false,
                        progress = if (sentences.isNotEmpty()) 1f / sentences.size else 0f
=======
    fun selectTopic(topic: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, selectedTopic = topic, errorMessage = null)
            }
            try {
                val dtos = speakingApiService.getSpeakingSentences(topic = topic, limit = 20)
                val items = dtos.map { dto ->
                    SpeakingSentenceItem(
                        id = dto.id,
                        sentence = dto.sentence,
                        translation = dto.translation,
                        difficulty = dto.difficulty,
                        topic = dto.topic
                    )
                }
                _uiState.update {
                    it.copy(
                        sentences = items,
                        currentIndex = 0,
                        isLoading = false,
                        hasResult = false,
                        spokenText = "",
                        score = 0,
                        feedback = ""
>>>>>>> a96e346 (fix ui + speaking)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
<<<<<<< HEAD
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load sentences: ${e.message}"
                    )
=======
                    it.copy(isLoading = false, errorMessage = "Could not load sentences")
>>>>>>> a96e346 (fix ui + speaking)
                }
            }
        }
    }

    fun nextSentence() {
<<<<<<< HEAD
        val currentState = _uiState.value
        val nextIndex = currentState.currentIndex + 1
        if (nextIndex < currentState.sentences.size) {
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    currentSentence = currentState.sentences[nextIndex],
                    hasResult = false,
                    spokenText = "",
                    score = 0,
                    feedback = "",
                    progress = (nextIndex + 1) / currentState.sentences.size.toFloat()
                )
            }
        }
    }

    fun previousSentence() {
        val currentState = _uiState.value
        val prevIndex = currentState.currentIndex - 1
        if (prevIndex >= 0) {
            _uiState.update {
                it.copy(
                    currentIndex = prevIndex,
                    currentSentence = currentState.sentences[prevIndex],
                    hasResult = false,
                    spokenText = "",
                    score = 0,
                    feedback = "",
                    progress = (prevIndex + 1) / currentState.sentences.size.toFloat()
                )
            }
        }
    }

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(appContext)) {
=======
        if (_uiState.value.currentIndex < _uiState.value.sentences.size - 1) {
>>>>>>> a96e346 (fix ui + speaking)
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    hasResult = false,
                    spokenText = "",
                    score = 0,
                    feedback = "",
                    errorMessage = null
                )
            }
        }
    }

    fun previousSentence() {
        if (_uiState.value.currentIndex > 0) {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex - 1,
                    hasResult = false,
                    spokenText = "",
                    score = 0,
                    feedback = "",
                    errorMessage = null
                )
            }
        }
    }

    fun backToTopics() {
        speechRecognizer?.stopListening()
        _uiState.update {
            SpeakingUiState(topics = it.topics)
        }
    }

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _uiState.update {
                it.copy(errorMessage = "This device does not support speech recognition")
            }
            return
        }

        _uiState.update {
            it.copy(
                isListening = true,
                hasResult = false,
                spokenText = "",
                errorMessage = null
            )
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: android.os.Bundle?) {
                    val matches = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spoken = matches?.firstOrNull() ?: ""
                    processResult(spoken)
                }

                override fun onError(error: Int) {
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH      -> "Could not hear you, please try again"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timeout, please speak louder"
                        SpeechRecognizer.ERROR_AUDIO         -> "Microphone error"
                        else                                  -> "Recognition error ($error)"
                    }
                    _uiState.update { it.copy(isListening = false, errorMessage = msg) }
                }

                override fun onReadyForSpeech(params: android.os.Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    _uiState.update { it.copy(isListening = false) }
                }
                override fun onPartialResults(partialResults: android.os.Bundle?) {}
                override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

<<<<<<< HEAD
    fun processResult(spoken: String) {
        val currentSentence = _uiState.value.currentSentence
        if (currentSentence == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isListening = false,
                    errorMessage = "No sentence selected"
                )
            }
            return
        }

        val sampleText = currentSentence.safeText
        val calculatedScore = calculateScore(sampleText, spoken)
        val calculatedFeedback = getFeedback(calculatedScore)
=======
    private fun processResult(spoken: String) {
        val sample = _uiState.value.currentSentence?.sentence ?: return
        val score = calculateScore(sample, spoken)
        val isMatched = score >= 70
        val feedback = getFeedback(score)
>>>>>>> a96e346 (fix ui + speaking)

        _uiState.update {
            it.copy(
                spokenText = spoken,
                score = score,
                feedback = feedback,
                hasResult = true,
                isListening = false
            )
        }

        viewModelScope.launch {
<<<<<<< HEAD
            savePracticeResult(
                sentenceId = currentSentence.id,
                sample = sampleText,
                spoken = spoken,
                score = calculatedScore
=======
            // Lưu vào Room DB
            speakingDao.insertSpeakingPractice(
                SpeakingPracticeEntity(
                    userId      = currentUserId,
                    lessonId    = null,
                    targetText  = sample,
                    spokenText  = spoken,
                    isMatched   = isMatched,
                    score       = score,
                    createdAt   = System.currentTimeMillis()
                )
>>>>>>> a96e346 (fix ui + speaking)
            )

            // Gửi lên backend — không block UI nếu lỗi
            try {
                speakingApiService.saveSpeakingResult(
                    SpeakingResultRequest(
                        targetText = sample,
                        spokenText = spoken,
                        score      = score,
                        isMatched  = isMatched,
                        lessonId   = null
                    )
                )
            } catch (_: Exception) {}
        }
    }

<<<<<<< HEAD
    fun calculateScore(sample: String, spoken: String): Int {
        val sampleWords = normalize(sample)
        val spokenWords = normalize(spoken)

=======
    private fun calculateScore(sample: String, spoken: String): Int {
        val normalize = { s: String ->
            s.lowercase().replace(Regex("[^a-z0-9 ]"), "").trim()
        }
        val sampleWords = normalize(sample).split(" ").filter { it.isNotEmpty() }
        val spokenWords  = normalize(spoken).split(" ").filter { it.isNotEmpty() }
>>>>>>> a96e346 (fix ui + speaking)
        if (sampleWords.isEmpty()) return 0
        val matched = sampleWords.count { it in spokenWords }
        return ((matched.toFloat() / sampleWords.size) * 100).toInt().coerceIn(0, 100)
    }

    private fun getFeedback(score: Int) = when {
        score >= 90 -> "Excellent! Perfect pronunciation!"
        score >= 70 -> "Great job! Almost perfect."
        score >= 50 -> "Not bad, keep practicing!"
        else        -> "Keep going, don't give up!"
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
<<<<<<< HEAD

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit

            override fun onBeginningOfSpeech() = Unit

            override fun onRmsChanged(rmsdB: Float) = Unit

            override fun onBufferReceived(buffer: ByteArray?) = Unit

            override fun onEndOfSpeech() {
                _uiState.update { it.copy(isListening = false) }
            }

            override fun onError(error: Int) {
                _uiState.update {
                    it.copy(
                        isListening = false,
                        errorMessage = recognitionErrorMessage(error)
                    )
                }
            }

            override fun onResults(results: Bundle?) {
                val spoken = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()

                if (spoken.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isListening = false,
                            errorMessage = "Could not hear you, please try again"
                        )
                    }
                } else {
                    processResult(spoken)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) = Unit

            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        }
    }

    private suspend fun savePracticeResult(
        sentenceId: Int,
        sample: String,
        spoken: String,
        score: Int
    ) {
        val userId = appDataStore.userId.first()

        if (userId <= 0) {
            _uiState.update {
                it.copy(errorMessage = "Could not save practice result")
            }
            return
        }

        try {
            ensureLocalUserExists(userId)

            speakingPracticeDao.insertSpeakingPractice(
                SpeakingPracticeEntity(
                    userId = userId,
                    targetText = sample,
                    spokenText = spoken,
                    isMatched = score >= 70,
                    score = score,
                    createdAt = System.currentTimeMillis()
                )
            )
        } catch (_: Exception) {
            _uiState.update {
                it.copy(errorMessage = "Could not save practice result")
            }
        }
    }

    private suspend fun ensureLocalUserExists(userId: Int) {
        if (userDao.getUserById(userId) != null) return

        val userName = appDataStore.userName.first().ifBlank { "Learner" }
        val userEmail = appDataStore.userEmail.first().ifBlank {
            "user$userId@local"
        }

        userDao.insertUser(
            UserEntity(
                id = userId,
                name = userName,
                email = userEmail,
                passwordHash = "",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private fun normalize(text: String): List<String> {
        return text
            .lowercase(Locale.US)
            .replace(Regex("[^a-z0-9\\s]"), "")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
    }

    private fun getFeedback(score: Int): String {
        return when (score) {
            in 90..100 -> "Excellent! Perfect pronunciation!"
            in 70..89 -> "Great job! Almost perfect."
            in 50..69 -> "Not bad, keep practicing!"
            else -> "Keep going, don't give up!"
        }
    }

    private fun recognitionErrorMessage(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "Could not hear you, please try again"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timeout, please speak louder"
            SpeechRecognizer.ERROR_AUDIO -> "Microphone error"
            else -> "Recognition error ($error)"
        }
    }
}
=======
}
>>>>>>> a96e346 (fix ui + speaking)
