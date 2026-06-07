package com.example.englishlearningapp.data.remote.api

import com.example.englishlearningapp.data.remote.dto.SpeakingResultRequest
import com.example.englishlearningapp.data.remote.dto.SpeakingResultResponse
import com.example.englishlearningapp.data.remote.dto.SpeakingSentenceDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SpeakingApiService {

    @GET("speaking/topics")
    suspend fun getSpeakingTopics(): List<String>

    @GET("speaking/sentences")
    suspend fun getSpeakingSentences(
        @Query("topic")      topic: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("limit")      limit: Int = 20
    ): List<SpeakingSentenceDto>

    @POST("speaking/practices")
    suspend fun saveSpeakingResult(
        @Body body: SpeakingResultRequest
    ): SpeakingResultResponse
}