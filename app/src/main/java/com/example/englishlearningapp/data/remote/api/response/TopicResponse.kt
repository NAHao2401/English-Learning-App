package com.example.englishlearningapp.data.remote.api.response

data class TopicResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val icon_url: String?,
    val level: String?
)