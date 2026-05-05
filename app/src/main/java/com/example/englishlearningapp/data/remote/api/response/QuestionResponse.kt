package com.example.englishlearningapp.data.remote.api.response

data class QuestionResponse(
    val id: Int,
    val lesson_id: Int,
    val question_type: String,
    val question_text: String,
    val audio_url: String?,
    val explanation: String?,
    val question_order: Int?,
    val answer_options: List<AnswerOptionResponse>
)

data class AnswerOptionResponse(
    val id: Int,
    val option_text: String,
    val option_order: Int?
)