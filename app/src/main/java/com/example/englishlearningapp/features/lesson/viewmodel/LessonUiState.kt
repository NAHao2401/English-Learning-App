package com.example.englishlearningapp.features.lesson.viewmodel

import com.example.englishlearningapp.data.remote.api.response.LessonResponse
import com.example.englishlearningapp.data.remote.api.response.QuestionResponse
import com.example.englishlearningapp.data.remote.api.response.SubmitLessonResponse
import com.example.englishlearningapp.data.remote.api.response.TopicResponse

data class LessonUiState(
    val topics: List<TopicResponse> = emptyList(),
    val lessons: List<LessonResponse> = emptyList(),
    val selectedLesson: LessonResponse? = null,
    val questions: List<QuestionResponse> = emptyList(),
    val selectedAnswers: Map<Int, String> = emptyMap(),
    val submitResult: SubmitLessonResponse? = null,

    val backendCompletionPercent: Int = 0,
    val isSavingAnswer: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)