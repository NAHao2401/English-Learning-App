package com.example.englishlearningapp.data.remote.api

import com.example.englishlearningapp.data.remote.dto.SpeakingResultRequest
import com.example.englishlearningapp.data.remote.dto.SpeakingSentenceDto
import com.example.englishlearningapp.data.remote.dto.SpeakingTopicDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SpeakingApiService {

    @GET("speaking/topics")
    suspend fun getSpeakingTopics(): List<SpeakingTopicDto>

    @GET("speaking/topics/{topicId}/sentences")
    suspend fun getSpeakingSentences(
        @Path("topicId") topicId: Int
    ): List<SpeakingSentenceDto>

    @POST("speaking/result")
    suspend fun saveSpeakingResult(
        @Body body: SpeakingResultRequest
    ): Response<Unit>
}