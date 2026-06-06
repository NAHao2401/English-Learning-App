package com.example.englishlearningapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SpeakingTopicDto(
    @SerializedName("id")    val id: Int,
    @SerializedName("name")  val name: String?,
    @SerializedName("sentence_count") val sentenceCount: Int
) {
    val displayName: String
        get() = name?.takeIf { it.isNotBlank() } ?: "Untitled topic"
}

data class SpeakingSentenceDto(
    @SerializedName("id")         val id: Int,
    @SerializedName("text")       val text: String?,
    @SerializedName("hint")       val hint: String?,
    @SerializedName("difficulty") val difficulty: String?
) {
    val safeText: String
        get() = text.orEmpty()

    val displayText: String
        get() = safeText.takeIf { it.isNotBlank() } ?: "No sentence available"

    val displayDifficulty: String
        get() = difficulty?.takeIf { it.isNotBlank() } ?: "Unknown"
}

data class SpeakingResultRequest(
    @SerializedName("question_id")  val questionId: Int,
    @SerializedName("spoken_text")  val spokenText: String,
    @SerializedName("score")        val score: Int
)