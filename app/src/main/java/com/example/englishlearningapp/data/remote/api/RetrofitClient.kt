package com.example.englishlearningapp.data.remote.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://127.0.0.1:8000/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val vocabApiService: VocabApiService by lazy {
        retrofit.create(VocabApiService::class.java)
    }

    val lessonApiService: LessonApiService by lazy {
        retrofit.create(LessonApiService::class.java)
    }

    val progressApiService: ProgressApiService by lazy {
        retrofit.create(ProgressApiService::class.java)
    }
}