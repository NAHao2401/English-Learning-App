package com.example.englishlearningapp.features.lesson.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonViewModel : ViewModel() {

    private val repository = LessonRepository()

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

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

    fun selectAnswer(questionId: Int, answer: String) {
        _uiState.value = _uiState.value.copy(
            selectedAnswers = _uiState.value.selectedAnswers + (questionId to answer)
        )
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
                errorMessage = "Please answer all questions before submitting"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = repository.submitLesson(
                lessonId = lessonId,
                answers = _uiState.value.selectedAnswers
            )

            _uiState.value = result.fold(
                onSuccess = { submitResult ->
                    _uiState.value.copy(
                        submitResult = submitResult,
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