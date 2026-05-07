package com.example.englishlearningapp.features.lesson.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.repository.LessonRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonViewModel : ViewModel() {

    private val repository = LessonRepository()

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    private val saveAnswerJobs = mutableMapOf<Int, Job>()

    fun loadTopics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = repository.getTopics()

            _uiState.value = result.fold(
                onSuccess = { topics ->
                    _uiState.value.copy(
                        topics = topics,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }

    fun loadLessonsByTopic(topicId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                lessons = emptyList()
            )

            val result = repository.getLessons(
                topicId = topicId,
                page = 1,
                limit = 20
            )

            _uiState.value = result.fold(
                onSuccess = { lessons ->
                    _uiState.value.copy(
                        lessons = lessons,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }

    fun loadLessonDetail(lessonId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                selectedLesson = null
            )

            val result = repository.getLessonDetail(lessonId)

            _uiState.value = result.fold(
                onSuccess = { lesson ->
                    _uiState.value.copy(
                        selectedLesson = lesson,
                        backendCompletionPercent = lesson.completion_percent,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }

    fun loadQuestions(lessonId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                questions = emptyList(),
                selectedAnswers = emptyMap(),
                submitResult = null
            )

            val result = repository.getLessonQuestions(lessonId)

            _uiState.value = result.fold(
                onSuccess = { questions ->
                    _uiState.value.copy(
                        questions = questions,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }

    fun selectAnswer(
        lessonId: Int,
        questionId: Int,
        answer: String
    ) {
        _uiState.value = _uiState.value.copy(
            selectedAnswers = _uiState.value.selectedAnswers + (questionId to answer),
            errorMessage = null
        )

        saveAnswerJobs[questionId]?.cancel()

        saveAnswerJobs[questionId] = viewModelScope.launch {
            delay(500)

            val trimmedAnswer = answer.trim()

            if (trimmedAnswer.isBlank()) {
                return@launch
            }

            _uiState.value = _uiState.value.copy(isSavingAnswer = true)

            val result = repository.saveAnswer(
                lessonId = lessonId,
                questionId = questionId,
                answer = trimmedAnswer
            )

            _uiState.value = result.fold(
                onSuccess = { saveResult ->
                    _uiState.value.copy(
                        backendCompletionPercent = saveResult.completion_percent,
                        isSavingAnswer = false,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isSavingAnswer = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }

    private suspend fun syncAllAnswersBeforeSubmit(
        lessonId: Int,
        answers: Map<Int, String>
    ): Result<Unit> {
        for ((questionId, answer) in answers) {
            val result = repository.saveAnswer(
                lessonId = lessonId,
                questionId = questionId,
                answer = answer
            )

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Cannot save answers")
                )
            }
        }

        return Result.success(Unit)
    }

    fun submitLesson(
        lessonId: Int,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        if (state.questions.isNotEmpty() &&
            state.selectedAnswers.size < state.questions.size
        ) {
            _uiState.value = state.copy(
                errorMessage = "Please answer all questions before completing the lesson"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val syncResult = syncAllAnswersBeforeSubmit(
                lessonId = lessonId,
                answers = state.selectedAnswers
            )

            if (syncResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = syncResult.exceptionOrNull()?.message
                        ?: "Cannot save answers before submitting lesson"
                )
                return@launch
            }

            val result = repository.submitLesson(
                lessonId = lessonId
            )

            _uiState.value = result.fold(
                onSuccess = { submitResult ->
                    _uiState.value.copy(
                        submitResult = submitResult,
                        backendCompletionPercent = submitResult.completion_percent,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            )

            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearLessonState() {
        _uiState.value = LessonUiState()
    }
}