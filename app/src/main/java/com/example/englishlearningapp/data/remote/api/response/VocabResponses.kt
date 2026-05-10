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

data class RateVocabRequest(
    @SerializedName("vocabulary_id") val vocabularyId: Int,
    val rating: Int
)

data class UserVocabularyResponse(
    @SerializedName("vocabulary_id") val vocabularyId: Int = 0,
    @SerializedName("is_saved") val isSaved: Boolean = false,
    @SerializedName("mastery_level") val masteryLevel: Int = 0,
    @SerializedName("last_reviewed_at") val lastReviewedAt: String? = null,
    @SerializedName("review_count") val reviewCount: Int = 0,
    @SerializedName("next_review_at") val nextReviewAt: String? = null,
    val id: Int = 0
)

data class TopicStudyResponse(
    @SerializedName("topic_id") val topicId: Int = 0,
    @SerializedName("total_words") val totalWords: Int = 0,
    @SerializedName("new_words") val newWords: List<VocabularyResponse> = emptyList(),
    @SerializedName("due_review_words") val dueReviewWords: List<VocabularyResponse> = emptyList(),
    @SerializedName("learned_count") val learnedCount: Int = 0
)

data class MasteryStatsResponse(
    @SerializedName("level_1") val level1: Int = 0,
    @SerializedName("level_2") val level2: Int = 0,
    @SerializedName("level_3") val level3: Int = 0,
    @SerializedName("level_4") val level4: Int = 0,
    @SerializedName("level_5") val level5: Int = 0
)

data class VocabOverviewResponse(
    @SerializedName("learned_count") val learnedCount: Int = 0,
    @SerializedName("due_review_count") val dueReviewCount: Int = 0,
    @SerializedName("mastery_stats") val masteryStats: MasteryStatsResponse = MasteryStatsResponse()
)

data class LearnedVocabItem(
    @SerializedName("vocabulary_id") val vocabularyId: Int = 0,
    val word: String = "",
    val meaning: String = "",
    val pronunciation: String? = null,
    @SerializedName("example_sentence") val exampleSentence: String? = null,
    @SerializedName("mastery_level") val masteryLevel: Int = 0,
    @SerializedName("review_count") val reviewCount: Int = 0,
    @SerializedName("last_reviewed_at") val lastReviewedAt: String? = null,
    @SerializedName("next_review_at") val nextReviewAt: String? = null,
    @SerializedName("is_due") val isDue: Boolean = false
)

data class LearnedVocabListResponse(
    val items: List<LearnedVocabItem> = emptyList(),
    val total: Int = 0,
    @SerializedName("due_count") val dueCount: Int = 0
)

