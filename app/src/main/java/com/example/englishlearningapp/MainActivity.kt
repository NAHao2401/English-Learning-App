package com.example.englishlearningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.navigation.AppNavGraph
import com.example.englishlearningapp.navigation.Screen
import com.example.englishlearningapp.ui.theme.EnglishLearningAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        RetrofitClient.initialize(applicationContext)

        val appDataStore = AppDataStore(applicationContext)

        setContent {
            EnglishLearningAppTheme {
                val isLoggedIn by appDataStore.isLoggedIn.collectAsState(initial = false)

                AppNavGraph(
                    context = applicationContext,
                    startDestination = if (isLoggedIn) {
                        Screen.Home.route
                    } else {
                        Screen.Login.route
                    }
                )
            }
        }
    }
}