package com.example.englishlearningapp.features.usertopic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.englishlearningapp.features.vocab.viewmodel.CefrLevelViewModel

class UserTopicViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserTopicViewModel::class.java)) {
            return UserTopicViewModel(context.applicationContext) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}