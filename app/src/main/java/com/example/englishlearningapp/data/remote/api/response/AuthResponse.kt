package com.example.englishlearningapp.data.remote.api.response

data class LoginResponse(
    val access_token: String,
    val refresh_token: String? = null,
    val token_type: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

data class RegisterResponse(
    val message: String
)