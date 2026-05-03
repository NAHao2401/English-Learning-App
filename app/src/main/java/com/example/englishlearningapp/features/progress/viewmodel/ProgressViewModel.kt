package com.example.englishlearningapp.features.progress.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.remote.api.response.ProgressSummaryResponse
import com.example.englishlearningapp.data.repository.ProgressRepository
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {

    private val repository = ProgressRepository()

    var summary by mutableStateOf<ProgressSummaryResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadProgressSummary() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = repository.getProgressSummary()

            result
                .onSuccess { summary = it }
                .onFailure { errorMessage = it.message }

            isLoading = false
        }
    }
}