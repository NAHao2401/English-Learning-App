package com.example.englishlearningapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SpeakingTopicDto(
    @SerializedName("id")    val id: Int,
    @SerializedName("name")  val name: String,
    @SerializedName("sentence_count") val sentenceCount: Int
)

data class SpeakingSentenceDto(
    @SerializedName("id")         val id: Int,
    @SerializedName("text")       val text: String,
    @SerializedName("hint")       val hint: String?,
    @SerializedName("difficulty") val difficulty: String
)

data class SpeakingResultRequest(
    @SerializedName("question_id")  val questionId: Int,
    @SerializedName("spoken_text")  val spokenText: String,
    @SerializedName("score")        val score: Int
)