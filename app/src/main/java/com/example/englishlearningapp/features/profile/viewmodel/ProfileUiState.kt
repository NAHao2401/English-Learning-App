package com.example.englishlearningapp.features.profile.viewmodel

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isDarkMode: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isChangingPassword: Boolean = false
)