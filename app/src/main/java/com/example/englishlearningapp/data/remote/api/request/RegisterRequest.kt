package com.example.englishlearningapp.data.remote.api.request

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)