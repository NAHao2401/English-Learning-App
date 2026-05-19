package com.example.englishlearningapp.features.profile.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    context: Context
) : ViewModel() {

    private val appDataStore = AppDataStore(context.applicationContext)
    private val authRepository = AuthRepository(context.applicationContext)

    private val isChangingPassword = MutableStateFlow(false)

    val uiState: StateFlow<ProfileUiState> =
        combine(
            appDataStore.userName,
            appDataStore.userEmail,
            appDataStore.isDarkMode,
            appDataStore.isSoundEnabled,
            isChangingPassword
        ) { userName, userEmail, isDarkMode, isSoundEnabled, isChangingPassword ->
            ProfileUiState(
                userName = userName,
                userEmail = userEmail,
                isDarkMode = isDarkMode,
                isSoundEnabled = isSoundEnabled,
                isChangingPassword = isChangingPassword
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState()
        )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            appDataStore.setDarkMode(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appDataStore.setSoundEnabled(enabled)
        }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            onResult(false, "Please fill in all fields")
            return
        }

        if (newPassword.length < 6) {
            onResult(false, "New password must be at least 6 characters")
            return
        }

        if (newPassword != confirmPassword) {
            onResult(false, "Confirm password does not match")
            return
        }

        if (currentPassword == newPassword) {
            onResult(false, "New password must be different from current password")
            return
        }

        viewModelScope.launch {
            isChangingPassword.value = true

            val result = authRepository.changePassword(
                currentPassword = currentPassword,
                newPassword = newPassword
            )

            isChangingPassword.value = false

            result.fold(
                onSuccess = { message ->
                    onResult(true, message)
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Change password failed")
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            appDataStore.logout()
            onLoggedOut()
        }
    }
}