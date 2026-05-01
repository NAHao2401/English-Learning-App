package com.example.englishlearningapp.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class SaveVocabularyRequest(
    @SerializedName("vocabulary_id") val vocabularyId: Int,
    @SerializedName("user_topic_id") val userTopicId: Int
)

