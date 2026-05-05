package com.example.englishlearningapp.core.util

import org.json.JSONObject

object ErrorParser {

    fun parse(errorBody: String?, fallbackMessage: String): String {
        if (errorBody.isNullOrBlank()) return fallbackMessage

        return try {
            val json = JSONObject(errorBody)

            when {
                json.has("message") -> json.optString("message", fallbackMessage)
                json.has("detail") -> json.optString("detail", fallbackMessage)
                else -> fallbackMessage
            }
        } catch (_: Exception) {
            fallbackMessage
        }
    }
}