package com.example.englishlearningapp.data.remote.api.request

data class SaveAnswerRequest(
    val question_id: Int,
    val answer: String
)