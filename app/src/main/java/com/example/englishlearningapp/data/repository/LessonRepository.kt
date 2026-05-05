package com.example.englishlearningapp.data.repository

import com.example.englishlearningapp.core.util.ErrorParser
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
                val message = ErrorParser.parse(
                    errorBody = response.errorBody()?.string(),
                    fallbackMessage = "Cannot load topics"
                )

                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error"))
        }
    }

    suspend fun getLessons(
        topicId: Int? = null,
        level: String? = null,
        page: Int = 1,
        limit: Int = 10
    ): Result<List<LessonResponse>> {
        return try {
            val response = lessonApi.getLessons(
                topicId = topicId,
                level = level,
                page = page,
                limit = limit
            )

            if (response.isSuccessful) {
                Result.success(response.body()?.items ?: emptyList())
            } else {
                val message = ErrorParser.parse(
                    errorBody = response.errorBody()?.string(),
                    fallbackMessage = "Cannot load lessons"
                )

                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error"))
        }
    }

    suspend fun getLessonDetail(lessonId: Int): Result<LessonResponse> {
        return try {
            val response = lessonApi.getLessonDetail(lessonId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Lesson detail body is null"))
                }
            } else {
                val message = ErrorParser.parse(
                    errorBody = response.errorBody()?.string(),
                    fallbackMessage = "Cannot load lesson detail"
                )

                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error"))
        }
    }

    suspend fun getLessonQuestions(lessonId: Int): Result<List<QuestionResponse>> {
        return try {
            val response = lessonApi.getLessonQuestions(lessonId)

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val message = ErrorParser.parse(
                    errorBody = response.errorBody()?.string(),
                    fallbackMessage = "Cannot load questions"
                )

                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error"))
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
                val message = ErrorParser.parse(
                    errorBody = response.errorBody()?.string(),
                    fallbackMessage = "Cannot submit lesson"
                )

                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error"))
        }
    }
}