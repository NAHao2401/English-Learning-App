package com.example.englishlearningapp.features.chat.model

data class Message(
    val role: String,       // "user" hoặc "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)