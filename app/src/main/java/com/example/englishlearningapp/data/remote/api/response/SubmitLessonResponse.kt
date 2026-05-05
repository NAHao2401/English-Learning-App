package com.example.englishlearningapp.data.remote.api.response

data class SubmitLessonResponse(
    val lesson_id: Int,
    val total_questions: Int,
    val correct_count: Int,
    val wrong_count: Int,
    val score: Int,
    val xp_earned: Int,
    val completion_percent: Int,
    val passed: Boolean,
    val streak_count: Int,
    val message: String
)