package com.example.englishlearningapp.features.progress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {

    private val repository = ProgressRepository()

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    fun loadProgress() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val summaryResult = repository.getProgressSummary()
            val lessonsResult = repository.getLessonProgresses()

            _uiState.value = if (summaryResult.isSuccess && lessonsResult.isSuccess) {
                _uiState.value.copy(
                    summary = summaryResult.getOrNull(),
                    lessonProgresses = lessonsResult.getOrNull() ?: emptyList(),
                    isLoading = false
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = summaryResult.exceptionOrNull()?.message
                        ?: lessonsResult.exceptionOrNull()?.message
                        ?: "Cannot load progress"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}