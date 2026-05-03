package com.example.englishlearningapp.data.remote.api.request

data class SubmitLessonRequest(
    val answers: List<SubmitAnswerRequest>
)

data class SubmitAnswerRequest(
    val question_id: Int,
    val answer: String
)