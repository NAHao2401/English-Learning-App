package com.example.englishlearningapp.features.chat.viewmodel

import com.example.englishlearningapp.features.chat.model.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputText: String = ""
)