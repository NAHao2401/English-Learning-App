package com.example.englishlearningapp.features.speaking.viewmodel

data class SpeakingUiState(
    val isLoading: Boolean = false,
    val sampleSentence: String = "The quick brown fox jumps over the lazy dog",
    val isListening: Boolean = false,
    val spokenText: String = "",
    val score: Int = 0,
    val feedback: String = "",
    val hasResult: Boolean = false,
    val errorMessage: String? = null
)
