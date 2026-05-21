package com.example.englishlearningapp.features.speaking.viewmodel

import com.example.englishlearningapp.data.remote.dto.SpeakingTopicDto
import com.example.englishlearningapp.data.remote.dto.SpeakingSentenceDto

data class SpeakingUiState(
    val isLoading: Boolean = false,
    val topics: List<SpeakingTopicDto> = emptyList(),
    val selectedTopic: SpeakingTopicDto? = null,
    val sentences: List<SpeakingSentenceDto> = emptyList(),
    val currentIndex: Int = 0,
    val currentSentence: SpeakingSentenceDto? = null,
    val isListening: Boolean = false,
    val spokenText: String = "",
    val score: Int = 0,
    val feedback: String = "",
    val hasResult: Boolean = false,
    val errorMessage: String? = null,
    val progress: Float = 0f
)
