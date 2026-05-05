package com.example.englishlearningapp

import android.app.Application
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EnglishLearningApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize RetrofitClient early with application context
        RetrofitClient.initialize(this)
    }
}
