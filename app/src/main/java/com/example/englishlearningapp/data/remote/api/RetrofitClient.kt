package com.example.englishlearningapp.data.remote.api

import android.content.Context
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://127.0.0.1:8000/"

    @Volatile
    private var initialized = false

    private lateinit var appDataStore: AppDataStore

    fun initialize(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            appDataStore = AppDataStore(context.applicationContext)
            initialized = true
        }
    }

    private fun authClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                val token = runBlocking {
                    if (!initialized) "" else appDataStore.accessToken.first()
                }
                val tokenType = runBlocking {
                    if (!initialized) "" else appDataStore.tokenType.first()
                }

                if (token.isNotBlank()) {
                    val authScheme = if (tokenType.isNotBlank()) tokenType else "Bearer"
                    requestBuilder.addHeader("Authorization", "$authScheme $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val vocabApiService: VocabApiService by lazy {
        retrofit.create(VocabApiService::class.java)
    }
}