package com.example.englishlearningapp.data.remote.api

import com.example.englishlearningapp.data.remote.api.request.SubmitLessonRequest
import com.example.englishlearningapp.data.remote.api.response.LessonResponse
import com.example.englishlearningapp.data.remote.api.response.PaginatedResponse
import com.example.englishlearningapp.data.remote.api.response.QuestionResponse
import com.example.englishlearningapp.data.remote.api.response.SubmitLessonResponse
import com.example.englishlearningapp.data.remote.api.response.TopicResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LessonApiService {

    @GET("lessons/topics")
    suspend fun getTopics(): Response<List<TopicResponse>>

    @GET("lessons")
    suspend fun getLessons(
        @Query("topic_id") topicId: Int? = null,
        @Query("level") level: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PaginatedResponse<LessonResponse>>

    @GET("lessons/{lessonId}")
    suspend fun getLessonDetail(
        @Path("lessonId") lessonId: Int
    ): Response<LessonResponse>

    @GET("lessons/{lessonId}/questions")
    suspend fun getLessonQuestions(
        @Path("lessonId") lessonId: Int
    ): Response<List<QuestionResponse>>

    @POST("lessons/{lessonId}/submit")
    suspend fun submitLesson(
        @Path("lessonId") lessonId: Int,
        @Body request: SubmitLessonRequest
    ): Response<SubmitLessonResponse>
}