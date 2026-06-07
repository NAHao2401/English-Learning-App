package com.example.englishlearningapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SpeakingSentenceDto(
    @SerializedName("id")          val id: Int,
    @SerializedName("sentence")    val sentence: String,
    @SerializedName("translation") val translation: String?,
    @SerializedName("difficulty")  val difficulty: String,
    @SerializedName("topic")       val topic: String?
)

data class SpeakingResultRequest(
    @SerializedName("target_text") val targetText: String,
    @SerializedName("spoken_text") val spokenText: String,
    @SerializedName("score")       val score: Int,
    @SerializedName("is_matched")  val isMatched: Boolean,
    @SerializedName("lesson_id")   val lessonId: Int?
)

data class SpeakingResultResponse(
    @SerializedName("message")   val message: String,
    @SerializedName("xp_earned") val xpEarned: Int
)