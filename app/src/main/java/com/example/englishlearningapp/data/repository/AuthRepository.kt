package com.example.englishlearningapp.data.repository

import android.content.Context
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.request.LoginRequest
import com.example.englishlearningapp.data.remote.api.request.RegisterRequest
import org.json.JSONObject

class AuthRepository(context: Context) {

    private val authApi = RetrofitClient.authApiService
    private val appDataStore = AppDataStore(context.applicationContext)

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
                        tokenType = body.token_type,
                        refreshToken = body.refresh_token
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val message = parseErrorMessage(errorBody) ?: "Login failed"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error"))
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val response = authApi.register(RegisterRequest(name, email, password))

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = parseErrorMessage(errorBody) ?: "Register failed"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error"))
        }
    }

    suspend fun logout() {
        appDataStore.logout()
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            if (errorBody.isNullOrBlank()) return null
            val json = JSONObject(errorBody)
            when {
                json.has("message") -> json.optString("message")
                json.has("detail") -> json.optString("detail")
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}