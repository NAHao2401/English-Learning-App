package com.example.englishlearningapp.data.repository

import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.response.ProgressLessonResponse
import com.example.englishlearningapp.data.remote.api.response.ProgressSummaryResponse

class ProgressRepository {

    private val progressApi = RetrofitClient.progressApiService

    suspend fun getProgressSummary(): Result<ProgressSummaryResponse> {
        return try {
            val response = progressApi.getProgressSummary()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Progress summary body is null"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Cannot load progress summary"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLessonProgresses(): Result<List<ProgressLessonResponse>> {
        return try {
            val response = progressApi.getLessonProgresses()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Cannot load lesson progresses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}