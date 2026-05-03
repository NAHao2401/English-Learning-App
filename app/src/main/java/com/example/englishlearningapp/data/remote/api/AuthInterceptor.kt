package com.example.englishlearningapp.data.remote.api

import android.content.Context
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    context: Context
) : Interceptor {

    private val appDataStore = AppDataStore(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            appDataStore.accessToken.first()
        }

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}