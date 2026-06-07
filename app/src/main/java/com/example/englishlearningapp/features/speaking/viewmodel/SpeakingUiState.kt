package com.example.englishlearningapp.features.speaking.viewmodel

import com.example.englishlearningapp.data.remote.dto.SpeakingTopicDto
import com.example.englishlearningapp.data.remote.dto.SpeakingSentenceDto

data class SpeakingUiState(
<<<<<<< HEAD
    val isLoading: Boolean = false,
    val topics: List<SpeakingTopicDto> = emptyList(),
    val selectedTopic: SpeakingTopicDto? = null,
    val sentences: List<SpeakingSentenceDto> = emptyList(),
    val currentIndex: Int = 0,
    val currentSentence: SpeakingSentenceDto? = null,
=======
    // ── Màn chọn topic ──────────────────────────────────────────────
    val topics: List<String> = emptyList(),
    val selectedTopic: String? = null,

    // ── Danh sách câu trong topic ────────────────────────────────────
    val sentences: List<SpeakingSentenceItem> = emptyList(),
    val currentIndex: Int = 0,

    // ── Trạng thái luyện nói ─────────────────────────────────────────
>>>>>>> a96e346 (fix ui + speaking)
    val isListening: Boolean = false,
    val spokenText: String = "",
    val score: Int = 0,
    val feedback: String = "",
    val hasResult: Boolean = false,
<<<<<<< HEAD
    val errorMessage: String? = null,
    val progress: Float = 0f
)
=======
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val currentSentence: SpeakingSentenceItem?
        get() = sentences.getOrNull(currentIndex)

    val progress: Float
        get() = if (sentences.isEmpty()) 0f
        else (currentIndex + 1).toFloat() / sentences.size

    val isFinished: Boolean
        get() = hasResult && currentIndex == sentences.size - 1
}

// Data class nhẹ dùng trong UI — map từ API response
data class SpeakingSentenceItem(
    val id: Int,
    val sentence: String,
    val translation: String?,
    val difficulty: String,
    val topic: String?
)
>>>>>>> a96e346 (fix ui + speaking)
