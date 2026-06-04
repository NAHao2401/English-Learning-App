package com.example.englishlearningapp.data.remote.api.request

data class DeviceTokenRequest(
    val token: String,
    val platform: String = "android"
)
