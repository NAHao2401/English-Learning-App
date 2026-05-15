package com.example.englishlearningapp.features.vocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VocabViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabViewModel::class.java)) {
            return VocabViewModel(context.applicationContext) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}    