package com.example.englishlearningapp.data.repository

import android.content.Context
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.request.LoginRequest
import com.example.englishlearningapp.data.remote.api.request.RegisterRequest

class AuthRepository(context: Context) {

    private val authApi = RetrofitClient.authApiService
    private val appDataStore = AppDataStore(context)

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = authApi.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    appDataStore.saveAuthSession(
                        userId = body.user.id,
                        userName = body.user.name,
                        userEmail = body.user.email,
                        accessToken = body.access_token,
                        refreshToken = body.refresh_token
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val response = authApi.register(RegisterRequest(name, email, password))

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Register failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}