package com.example.englishlearningapp.features.speaking.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.englishlearningapp.data.local.db.dao.SpeakingPracticeDao
import com.example.englishlearningapp.data.remote.api.SpeakingApiService

class SpeakingViewModelFactory(
    private val context: Context,
    private val speakingPracticeDao: SpeakingPracticeDao,
    private val apiService: SpeakingApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeakingViewModel::class.java)) {
            return SpeakingViewModel(
                context.applicationContext,
                speakingPracticeDao,
                apiService
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
