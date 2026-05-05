package com.example.englishlearningapp.data.remote.api.response

/**
 * DEPRECATED: This class has been consolidated into VocabResponses.kt
 * to avoid duplicate definitions. Use TopicResponse from VocabResponses.kt instead.
 */
@Deprecated("Use TopicResponse from VocabResponses.kt", level = DeprecationLevel.HIDDEN)
data class TopicResponseLegacy(
    val id: Int,
    val name: String,
    val description: String?,
    val icon_url: String?,
    val level: String?
)