package com.example.englishlearningapp.features.speaking.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.local.db.DatabaseProvider
import com.example.englishlearningapp.data.local.db.dao.SpeakingPracticeDao
import com.example.englishlearningapp.data.local.db.entity.SpeakingPracticeEntity
import com.example.englishlearningapp.data.local.db.entity.UserEntity
import com.example.englishlearningapp.data.remote.api.SpeakingApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class SpeakingViewModel(
    context: Context,
    private val speakingPracticeDao: SpeakingPracticeDao,
    private val apiService: SpeakingApiService
) : ViewModel() {

    private val appContext = context.applicationContext
    private val appDataStore = AppDataStore(appContext)
    private val userDao = DatabaseProvider.getDatabase(appContext).userDao()
    private var speechRecognizer: SpeechRecognizer? = null

    private val _uiState = MutableStateFlow(SpeakingUiState())
    val uiState: StateFlow<SpeakingUiState> = _uiState.asStateFlow()

    fun loadTopics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val topics = apiService.getSpeakingTopics()
                _uiState.update { it.copy(topics = topics, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load topics: ${e.message}"
                    )
                }
            }
        }
    }

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
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load sentences: ${e.message}"
                    )
                }
            }
        }
    }

    fun nextSentence() {
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
            _uiState.update {
                it.copy(
                    isListening = false,
                    errorMessage = "Speech recognition is not available on this device"
                )
            }
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
            setRecognitionListener(createRecognitionListener())
        }

        _uiState.update {
            it.copy(
                isListening = true,
                errorMessage = null,
                hasResult = false,
                spokenText = "",
                score = 0,
                feedback = ""
            )
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        }

        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

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

        _uiState.update {
            it.copy(
                isLoading = false,
                isListening = false,
                spokenText = spoken,
                score = calculatedScore,
                feedback = calculatedFeedback,
                hasResult = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            savePracticeResult(
                sentenceId = currentSentence.id,
                sample = sampleText,
                spoken = spoken,
                score = calculatedScore
            )
        }
    }

    fun calculateScore(sample: String, spoken: String): Int {
        val sampleWords = normalize(sample)
        val spokenWords = normalize(spoken)

        if (sampleWords.isEmpty()) return 0

        val matchingWords = sampleWords.count { sampleWord ->
            spokenWords.contains(sampleWord)
        }

        return ((matchingWords.toDouble() / sampleWords.size) * 100).toInt()
    }

    override fun onCleared() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        super.onCleared()
    }

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
