package com.example.englishlearningapp.data.remote.api.response

data class SaveAnswerResponse(
    val lesson_id: Int,
    val question_id: Int,
    val answered_count: Int,
    val total_questions: Int,
    val completion_percent: Int,
    val status: String,
    val is_correct: Boolean?
)