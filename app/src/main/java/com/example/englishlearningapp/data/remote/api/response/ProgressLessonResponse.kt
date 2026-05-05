package com.example.englishlearningapp.data.remote.api.response

data class ProgressLessonResponse(
    val lesson_id: Int,
    val title: String,
    val status: String,
    val completion_percent: Int,
    val highest_score: Int,
    val is_locked: Boolean
)