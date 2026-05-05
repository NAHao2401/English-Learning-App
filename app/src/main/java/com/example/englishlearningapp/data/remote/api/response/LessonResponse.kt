package com.example.englishlearningapp.data.remote.api.response

data class LessonResponse(
    val id: Int,
    val topic_id: Int,
    val title: String,
    val description: String?,
    val lesson_order: Int?,
    val difficulty: String?,
    val estimated_time: Int?,
    val is_locked: Boolean,
    val completion_percent: Int,
    val status: String
)