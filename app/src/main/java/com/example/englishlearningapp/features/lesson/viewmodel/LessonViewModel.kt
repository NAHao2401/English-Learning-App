package com.example.englishlearningapp.features.lesson.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.remote.api.response.LessonResponse
import com.example.englishlearningapp.data.remote.api.response.QuestionResponse
import com.example.englishlearningapp.data.remote.api.response.SubmitLessonResponse
import com.example.englishlearningapp.data.remote.api.response.TopicResponse
import com.example.englishlearningapp.data.repository.LessonRepository
import kotlinx.coroutines.launch

class LessonViewModel : ViewModel() {

    private val repository = LessonRepository()

    var topics by mutableStateOf<List<TopicResponse>>(emptyList())
        private set

    var lessons by mutableStateOf<List<LessonResponse>>(emptyList())
        private set

    var questions by mutableStateOf<List<QuestionResponse>>(emptyList())
        private set

    var selectedAnswers by mutableStateOf<Map<Int, String>>(emptyMap())
        private set

    var submitResult by mutableStateOf<SubmitLessonResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadTopics() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = repository.getTopics()

            result
                .onSuccess { topics = it }
                .onFailure { errorMessage = it.message }

            isLoading = false
        }
    }

    fun loadLessonsByTopic(topicId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            lessons = emptyList()

            val result = repository.getLessonsByTopic(topicId)

            result
                .onSuccess { lessons = it }
                .onFailure { errorMessage = it.message }

            isLoading = false
        }
    }

    fun loadQuestions(lessonId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            questions = emptyList()
            selectedAnswers = emptyMap()
            submitResult = null

            val result = repository.getLessonQuestions(lessonId)

            result
                .onSuccess { questions = it }
                .onFailure { errorMessage = it.message }

            isLoading = false
        }
    }

    fun selectAnswer(questionId: Int, answer: String) {
        selectedAnswers = selectedAnswers + (questionId to answer)
    }

    fun submitLesson(lessonId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = repository.submitLesson(
                lessonId = lessonId,
                answers = selectedAnswers
            )

            result
                .onSuccess {
                    submitResult = it
                    onSuccess()
                }
                .onFailure {
                    errorMessage = it.message
                }

            isLoading = false
        }
    }

    fun clearError() {
        errorMessage = null
    }
}