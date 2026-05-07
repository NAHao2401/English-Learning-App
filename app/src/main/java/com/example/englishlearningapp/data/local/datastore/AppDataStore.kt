package com.example.englishlearningapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "english_learning_preferences"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME) //Gắn vào Context dùng được ở mọi nơi

class AppDataStore(private val context: Context) {

    val userId: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.USER_ID] ?: -1
    }

    val userEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.USER_EMAIL] ?: ""
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.USER_NAME] ?: ""
    }

    val accessToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.ACCESS_TOKEN] ?: ""
    }

    val refreshToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.REFRESH_TOKEN] ?: ""
    }

    val tokenType: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.TOKEN_TYPE] ?: ""
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        val loggedIn = preferences[AppPreferences.IS_LOGGED_IN] ?: false
        val token = preferences[AppPreferences.ACCESS_TOKEN].orEmpty()
        loggedIn && token.isNotBlank()
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.IS_DARK_MODE] ?: false
    }

    val isSoundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.IS_SOUND_ENABLED] ?: true
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AppPreferences.HAS_COMPLETED_ONBOARDING] ?: false
    }

    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferences.USER_ID] = userId
        }
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferences.USER_EMAIL] = email
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferences.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferences.IS_DARK_MODE] = isDarkMode
        }
    }

    suspend fun setSoundEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferences.IS_SOUND_ENABLED] = isEnabled
        }
    }

    suspend fun setCompletedOnboarding(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferences.HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(AppPreferences.USER_ID)
            preferences.remove(AppPreferences.USER_EMAIL)
            preferences[AppPreferences.IS_LOGGED_IN] = false
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveAuthSession(
        userId: Int,
        userName: String,
        userEmail: String,
        accessToken: String,
        tokenType: String,
        refreshToken: String? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferences.USER_ID] = userId
            preferences[AppPreferences.USER_NAME] = userName
            preferences[AppPreferences.USER_EMAIL] = userEmail
            preferences[AppPreferences.ACCESS_TOKEN] = accessToken
            preferences[AppPreferences.TOKEN_TYPE] = tokenType
            preferences[AppPreferences.REFRESH_TOKEN] = refreshToken ?: ""
            preferences[AppPreferences.IS_LOGGED_IN] = true
        }
    }

    suspend fun clearAuthSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(AppPreferences.USER_ID)
            preferences.remove(AppPreferences.USER_NAME)
            preferences.remove(AppPreferences.USER_EMAIL)
            preferences.remove(AppPreferences.ACCESS_TOKEN)
            preferences.remove(AppPreferences.REFRESH_TOKEN)
            preferences.remove(AppPreferences.TOKEN_TYPE)
            preferences[AppPreferences.IS_LOGGED_IN] = false
        }
    }

    suspend fun logout() {
        clearAuthSession()
    }
}