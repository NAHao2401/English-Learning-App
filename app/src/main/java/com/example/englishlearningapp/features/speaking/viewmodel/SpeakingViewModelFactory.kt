package com.example.englishlearningapp.features.speaking.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.local.db.dao.SpeakingPracticeDao
import com.example.englishlearningapp.data.remote.api.SpeakingApiService

class SpeakingViewModelFactory(
    private val context: Context,
    private val speakingPracticeDao: SpeakingPracticeDao,
    private val speakingApiService: SpeakingApiService,
    private val appDataStore: AppDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeakingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpeakingViewModel(
                context.applicationContext,
                speakingPracticeDao,
                speakingApiService,
                appDataStore
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
