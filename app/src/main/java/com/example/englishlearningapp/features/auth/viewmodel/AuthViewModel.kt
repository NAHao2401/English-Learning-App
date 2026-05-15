package com.example.englishlearningapp.features.auth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(context: Context) : ViewModel() {

    private val repository = AuthRepository(context)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.login(email, password)

            _uiState.value = if (result.isSuccess) {
                AuthUiState(isLoginSuccess = true)
            } else {
                AuthUiState(errorMessage = result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in all fields")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = AuthUiState(errorMessage = "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.register(name, email, password)

            _uiState.value = if (result.isSuccess) {
                AuthUiState(isRegisterSuccess = true)
            } else {
                AuthUiState(errorMessage = result.exceptionOrNull()?.message ?: "Register failed")
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit = {}) {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
            onLoggedOut()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}