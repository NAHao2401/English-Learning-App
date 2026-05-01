package com.example.englishlearningapp.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class TopicResponse(
    val id: Int,
    val name: String,
    val description: String?,
    @SerializedName("icon_emoji") val iconEmoji: String?,
    val color: String?,
    val level: String?,
    @SerializedName("word_count") val wordCount: Int? = null
)

data class VocabularyResponse(
    val id: Int,
    @SerializedName("topic_id") val topicId: Int,
    val word: String,
    val meaning: String,
    val pronunciation: String?,
    @SerializedName("example_sentence") val exampleSentence: String?,
    @SerializedName("audio_url") val audioUrl: String?,
    val difficulty: String?
)

