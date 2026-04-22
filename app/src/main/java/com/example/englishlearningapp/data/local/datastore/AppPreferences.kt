package com.example.englishlearningapp.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppPreferences {

    val USER_ID = intPreferencesKey("user_id")

    val USER_EMAIL = stringPreferencesKey("user_email")

    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

    val IS_SOUND_ENABLED = booleanPreferencesKey("is_sound_enabled")

    val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")

    val ACCESS_TOKEN = stringPreferencesKey("access_token")

    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

    val USER_NAME = stringPreferencesKey("user_name")
}