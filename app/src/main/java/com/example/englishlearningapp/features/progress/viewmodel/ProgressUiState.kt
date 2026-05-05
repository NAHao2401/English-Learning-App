package com.example.englishlearningapp.features.progress.viewmodel

import com.example.englishlearningapp.data.remote.api.response.ProgressLessonResponse
import com.example.englishlearningapp.data.remote.api.response.ProgressSummaryResponse

data class ProgressUiState(
    val summary: ProgressSummaryResponse? = null,
    val lessonProgresses: List<ProgressLessonResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)