package com.example.englishlearningapp.data.repository

import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.request.SubmitAnswerRequest
import com.example.englishlearningapp.data.remote.api.request.SubmitLessonRequest
import com.example.englishlearningapp.data.remote.api.response.LessonResponse
import com.example.englishlearningapp.data.remote.api.response.QuestionResponse
import com.example.englishlearningapp.data.remote.api.response.SubmitLessonResponse
import com.example.englishlearningapp.data.remote.api.response.TopicResponse

class LessonRepository {

    private val lessonApi = RetrofitClient.lessonApiService

    suspend fun getTopics(): Result<List<TopicResponse>> {
        return try {
            val response = lessonApi.getTopics()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Cannot load topics"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLessonsByTopic(topicId: Int): Result<List<LessonResponse>> {
        return try {
            val response = lessonApi.getLessonsByTopic(topicId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Cannot load lessons"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLessonQuestions(lessonId: Int): Result<List<QuestionResponse>> {
        return try {
            val response = lessonApi.getLessonQuestions(lessonId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Cannot load questions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitLesson(
        lessonId: Int,
        answers: Map<Int, String>
    ): Result<SubmitLessonResponse> {
        return try {
            val request = SubmitLessonRequest(
                answers = answers.map { entry ->
                    SubmitAnswerRequest(
                        question_id = entry.key,
                        answer = entry.value
                    )
                }
            )

            val response = lessonApi.submitLesson(lessonId, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Submit response body is null"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Cannot submit lesson"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}