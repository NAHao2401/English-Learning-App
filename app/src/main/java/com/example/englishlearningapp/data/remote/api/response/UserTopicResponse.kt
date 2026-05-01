package com.example.englishlearningapp.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class UserTopicResponse(
    val id: Int,
    val name: String,
    val description: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("word_count") val wordCount: Int = 0,
    @SerializedName("learned_count") val learnedCount: Int = 0
)

