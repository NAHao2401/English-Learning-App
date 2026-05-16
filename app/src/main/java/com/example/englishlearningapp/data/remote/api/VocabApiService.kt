package com.example.englishlearningapp.data.remote.api

import com.example.englishlearningapp.data.remote.api.response.TopicResponse
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.data.remote.api.response.UserTopicResponse
import com.example.englishlearningapp.data.remote.api.response.UserTopicCreateRequest
import com.example.englishlearningapp.data.remote.api.response.SaveVocabularyRequest
import com.example.englishlearningapp.data.remote.api.response.SavedVocabularyResponse
import com.example.englishlearningapp.data.remote.api.response.RateVocabRequest
import com.example.englishlearningapp.data.remote.api.response.TopicStudyResponse
import com.example.englishlearningapp.data.remote.api.response.UserVocabularyResponse
import com.example.englishlearningapp.data.remote.api.response.VocabOverviewResponse
import com.example.englishlearningapp.data.remote.api.response.LearnedVocabListResponse
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST

interface VocabApiService {

    @GET("vocabularies/topics")
    suspend fun getTopics(): List<TopicResponse>

    @GET("vocabularies/all")
    suspend fun getAllVocabularies(@Query("level") level: String?): List<VocabularyResponse>

    @GET("vocabularies/search")
    suspend fun searchVocabularies(@Query("prefix") prefix: String): List<VocabularyResponse>

    @GET("vocabularies/progress/batch")
    suspend fun getBatchProgress(@Query("vocab_ids") vocabIds: String): Map<Int, UserVocabularyResponse>

    @GET("vocabularies/search")
    suspend fun searchVocabulariesByPrefix(@Query("prefix") prefix: String): List<VocabularyResponse>

    @GET("vocabularies/topic/{topic_id}")
    suspend fun getVocabulariesByTopic(@Path("topic_id") topicId: Int): List<VocabularyResponse>

    @GET("vocabularies/progress/topic/{topicId}")
    suspend fun getTopicProgress(@Path("topicId") topicId: Int): Map<Int, UserVocabularyResponse>

    @GET("vocabularies/progress/topic/{topicId}/study")
    suspend fun getStudySession(@Path("topicId") topicId: Int): TopicStudyResponse

    // User topics (My folders)
    @GET("vocabularies/user-topics")
    suspend fun getUserTopics(): List<UserTopicResponse>

    @POST("vocabularies/user-topics")
    suspend fun createUserTopic(@Body request: UserTopicCreateRequest): UserTopicResponse

    @GET("vocabularies/user-topics/{user_topic_id}/vocabularies")
    suspend fun getUserTopicVocabularies(@Path("user_topic_id") userTopicId: Int): List<VocabularyResponse>

    @DELETE("vocabularies/user-topics/{user_topic_id}/vocabularies/{vocabulary_id}")
    suspend fun deleteUserTopicVocabulary(
        @Path("user_topic_id") userTopicId: Int,
        @Path("vocabulary_id") vocabularyId: Int
    )

    @POST("vocabularies/save")
    suspend fun saveVocabulary(@Body request: SaveVocabularyRequest): SavedVocabularyResponse

    @POST("vocabularies/progress/rate")
    suspend fun rateVocabulary(@Body request: RateVocabRequest): UserVocabularyResponse

    @GET("vocabularies/overview")
    suspend fun getVocabOverview(): VocabOverviewResponse

    @GET("vocabularies/learned")
    suspend fun getLearnedVocabs(): LearnedVocabListResponse

    @GET("vocabularies/learned/practice-pool")
    suspend fun getLearnedPracticePool(): List<VocabularyResponse>

    @GET("vocabularies/user-topics/all-saved-words")
    suspend fun getAllUserTopicWords(): List<VocabularyResponse>
}


