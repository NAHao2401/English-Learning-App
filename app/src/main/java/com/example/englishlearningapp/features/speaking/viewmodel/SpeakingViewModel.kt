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
import com.example.englishlearningapp.data.local.db.dao.SpeakingPracticeDao
import com.example.englishlearningapp.data.local.db.entity.SpeakingPracticeEntity
import com.example.englishlearningapp.data.remote.api.SpeakingApiService
import com.example.englishlearningapp.data.remote.dto.SpeakingResultRequest
import com.google.gson.JsonElement
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpeakingViewModel(
    private val context: Context,
    private val speakingPracticeDao: SpeakingPracticeDao,
    private val speakingApiService: SpeakingApiService,
    private val appDataStore: AppDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeakingUiState())
    val uiState: StateFlow<SpeakingUiState> = _uiState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var currentUserId: Int = -1
    private var ignoreNextRecognitionError = false

    init {
        viewModelScope.launch {
            currentUserId = appDataStore.userId.first()
        }
    }

    fun loadTopics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val topics = speakingApiService.getSpeakingTopics()
                    .mapIndexedNotNull(::parseTopic)
                    .filter { it.id in 1..6 }
                    .take(6)
                _uiState.update {
                    it.copy(
                        topics = topics,
                        selectedTopic = null,
                        sentences = emptyList(),
                        currentIndex = 0,
                        hasResult = false,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Could not load topics: ${e.message}")
                }
            }
        }
    }

    fun selectTopic(topic: SpeakingTopicItem) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedTopic = topic,
                    isLoading = true,
                    errorMessage = null,
                    currentIndex = 0,
                    hasResult = false,
                    spokenText = "",
                    score = 0,
                    feedback = ""
                )
            }

            try {
                val sentences = speakingApiService.getSpeakingSentencesByTopicId(topic.id)
                    .mapNotNull { dto ->
                        val sentence = dto.sentence?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                        SpeakingSentenceItem(
                            id = dto.id,
                            sentence = sentence,
                            translation = dto.translation,
                            difficulty = dto.difficulty,
                            topic = dto.topic ?: topic.name
                        )
                    }

                _uiState.update {
                    it.copy(sentences = sentences, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Could not load sentences: ${e.message}")
                }
            }
        }
    }

    private fun parseTopic(index: Int, element: JsonElement): SpeakingTopicItem? {
        if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
            val name = element.asString.takeIf { it.isNotBlank() } ?: return null
            return SpeakingTopicItem(id = index + 1, name = name)
        }

        if (!element.isJsonObject) return null

        val json = element.asJsonObject
        val id = json.stringValue("id")?.toIntOrNull()
            ?: json.stringValue("topic_id")?.toIntOrNull()
            ?: index + 1
        val name = json.stringValue("topic")
            ?: json.stringValue("name")
            ?: json.stringValue("title")
            ?: return null

        return name.takeIf { it.isNotBlank() }?.let { SpeakingTopicItem(id = id, name = it) }
    }

    private fun com.google.gson.JsonObject.stringValue(key: String): String? {
        return get(key)?.takeIf { !it.isJsonNull }?.asString
    }

    fun nextSentence() {
        if (_uiState.value.currentIndex < _uiState.value.sentences.lastIndex) {
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
        ignoreNextRecognitionError = true
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
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

        ignoreNextRecognitionError = false
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(createRecognitionListener())
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        ignoreNextRecognitionError = true
        speechRecognizer?.cancel()
        _uiState.update { it.copy(isListening = false, errorMessage = null) }
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
                if (ignoreNextRecognitionError) {
                    ignoreNextRecognitionError = false
                    _uiState.update { it.copy(isListening = false, errorMessage = null) }
                    return
                }

                _uiState.update {
                    it.copy(isListening = false, errorMessage = recognitionErrorMessage(error))
                }
            }

            override fun onResults(results: Bundle?) {
                val spoken = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()

                if (spoken.isBlank()) {
                    _uiState.update {
                        it.copy(isListening = false, errorMessage = "Could not hear you, please try again")
                    }
                } else {
                    processResult(spoken)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        }
    }

    private fun processResult(spoken: String) {
        val sample = _uiState.value.currentSentence?.sentence
        if (sample == null) {
            _uiState.update {
                it.copy(isListening = false, errorMessage = "No sentence selected")
            }
            return
        }

        val score = calculateScore(sample, spoken)
        val isMatched = score >= 70
        val feedback = getFeedback(score)

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
            savePracticeResult(sample = sample, spoken = spoken, score = score, isMatched = isMatched)
        }
    }

    private suspend fun savePracticeResult(
        sample: String,
        spoken: String,
        score: Int,
        isMatched: Boolean
    ) {
        if (currentUserId <= 0) {
            currentUserId = appDataStore.userId.first()
        }

        if (currentUserId > 0) {
            try {
                speakingPracticeDao.insertSpeakingPractice(
                    SpeakingPracticeEntity(
                        userId = currentUserId,
                        lessonId = null,
                        targetText = sample,
                        spokenText = spoken,
                        isMatched = isMatched,
                        score = score,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "Could not save practice result locally") }
            }
        }

        try {
            speakingApiService.saveSpeakingResult(
                SpeakingResultRequest(
                    targetText = sample,
                    spokenText = spoken,
                    score = score,
                    isMatched = isMatched,
                    lessonId = null
                )
            )
        } catch (_: Exception) {
            _uiState.update { it.copy(errorMessage = "Could not sync practice result") }
        }
    }

    private fun calculateScore(sample: String, spoken: String): Int {
        val sampleWords = normalize(sample)
        val spokenWords = normalize(spoken).toSet()

        if (sampleWords.isEmpty()) return 0

        val matched = sampleWords.count { it in spokenWords }
        return ((matched.toFloat() / sampleWords.size) * 100).toInt().coerceIn(0, 100)
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

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
}
