package com.example.englishlearningapp.data.remote.api

import com.example.englishlearningapp.data.remote.api.response.ProgressLessonResponse
import com.example.englishlearningapp.data.remote.api.response.ProgressSummaryResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProgressApiService {

    @GET("progress/me/summary")
    suspend fun getProgressSummary(): Response<ProgressSummaryResponse>

    @GET("progress/me/lessons")
    suspend fun getLessonProgresses(): Response<List<ProgressLessonResponse>>
}