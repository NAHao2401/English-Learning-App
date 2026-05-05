package com.example.englishlearningapp.data.remote.api

import android.content.Context
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://127.0.0.1:8000/"

    @Volatile
    private var appDataStoreInstance: AppDataStore? = null
    private var applicationContext: Context? = null

    /**
     * Initialize RetrofitClient with application context.
     * This should be called as early as possible, ideally in Application.onCreate()
     */
    fun initialize(context: Context) {
        synchronized(this) {
            if (appDataStoreInstance == null) {
                applicationContext = context.applicationContext
                appDataStoreInstance = AppDataStore(context.applicationContext)
            }
        }
    }

    /**
     * Lazy initialization of Retrofit instance.
     * Automatically initializes on first access.
     */
    private val retrofit: Retrofit by lazy {
        val context = applicationContext ?: throw IllegalStateException(
            "RetrofitClient not initialized. Call RetrofitClient.initialize(context) first, " +
            "or ensure it's called before accessing any API services."
        )

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        Retrofit.Builder()
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