package com.example.englishlearningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.englishlearningapp.features.auth.ui.LoginScreen
import com.example.englishlearningapp.features.auth.ui.RegisterScreen
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModel
import com.example.englishlearningapp.ui.theme.EnglishLearningAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = AuthViewModel(this)

        setContent {
            EnglishLearningAppTheme {
                var showRegister by remember { mutableStateOf(false) }

                if (showRegister) {
                    RegisterScreen(
                        viewModel = authViewModel,
                        onNavigateToLogin = { showRegister = false },
                        onRegisterSuccess = { showRegister = false }
                    )
                } else {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { showRegister = true },
                        onLoginSuccess = {

                        }
                    )
                }
            }
        }
    }
}