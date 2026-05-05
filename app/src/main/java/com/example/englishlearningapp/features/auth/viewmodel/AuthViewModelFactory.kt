package com.example.englishlearningapp.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * DEPRECATED: This factory is no longer used. The app now uses Hilt for dependency injection.
 * Use hiltViewModel() in Compose or @HiltViewModel in ViewModel classes instead.
 *
 * Kept for backward compatibility / to avoid breaking imports in other files.
 */
@Deprecated("Use Hilt dependency injection instead")
class AuthViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        throw UnsupportedOperationException(
            "AuthViewModelFactory is deprecated. Use Hilt dependency injection instead. " +
            "Use hiltViewModel() in Compose or @HiltViewModel with @Inject constructor in ViewModel classes."
        )
    }
}