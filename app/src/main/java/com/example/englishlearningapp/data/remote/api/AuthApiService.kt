package com.example.englishlearningapp.data.remote.api

import com.example.englishlearningapp.data.remote.api.request.LoginRequest
import com.example.englishlearningapp.data.remote.api.request.RegisterRequest
import com.example.englishlearningapp.data.remote.api.response.LoginResponse
import com.example.englishlearningapp.data.remote.api.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}