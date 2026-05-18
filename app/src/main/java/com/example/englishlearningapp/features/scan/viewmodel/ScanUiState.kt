package com.example.englishlearningapp.features.scan.viewmodel

import android.net.Uri

data class ExtractedWord(
    val word: String,
    val meaningVi: String,
    val example: String
)

data class ScanUiState(
    val selectedImageUri: Uri? = null,
    val isAnalyzing: Boolean = false,
    val extractedWords: List<ExtractedWord> = emptyList(),
    val isSaving: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val errorMessage: String? = null
)