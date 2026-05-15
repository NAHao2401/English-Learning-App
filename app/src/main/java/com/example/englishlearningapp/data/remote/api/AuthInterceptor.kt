package com.example.englishlearningapp.data.remote.api

import android.content.Context
import com.example.englishlearningapp.core.session.SessionManager
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    context: Context
) : Interceptor {

    private val appContext = context.applicationContext
    private val appDataStore = AppDataStore(appContext)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url().encodedPath()
        val isAuthRequest = path.contains("/auth/login") || path.contains("/auth/register")

        val requestBuilder = originalRequest.newBuilder()

        if (!isAuthRequest) {
            val token = runBlocking {
                appDataStore.accessToken.first()
            }

            if (token.isNotBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code() == 401 && !isAuthRequest) {
            SessionManager.handleUnauthorized(appContext)
        }

        return response
    }
}