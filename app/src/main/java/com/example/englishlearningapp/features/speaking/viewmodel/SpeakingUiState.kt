package com.example.englishlearningapp.features.speaking.viewmodel

data class SpeakingUiState(
    val topics: List<SpeakingTopicItem> = emptyList(),
    val selectedTopic: SpeakingTopicItem? = null,
    val sentences: List<SpeakingSentenceItem> = emptyList(),
    val currentIndex: Int = 0,
    val isListening: Boolean = false,
    val spokenText: String = "",
    val score: Int = 0,
    val feedback: String = "",
    val hasResult: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val currentSentence: SpeakingSentenceItem?
        get() = sentences.getOrNull(currentIndex)

    val progress: Float
        get() = if (sentences.isEmpty()) 0f else (currentIndex + 1).toFloat() / sentences.size

    val isFinished: Boolean
        get() = hasResult && currentIndex == sentences.lastIndex
}

data class SpeakingTopicItem(
    val id: Int,
    val name: String
)

data class SpeakingSentenceItem(
    val id: Int,
    val sentence: String,
    val translation: String?,
    val difficulty: String,
    val topic: String?
)
