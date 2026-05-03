package com.example.englishlearningapp.data.remote.api.response

data class ProgressSummaryResponse(
    val total_xp: Int,
    val streak_count: Int,
    val current_level: String,
    val completed_lessons: Int,
    val total_lessons: Int,
    val completion_percent: Int,
    val study_days: Int
)