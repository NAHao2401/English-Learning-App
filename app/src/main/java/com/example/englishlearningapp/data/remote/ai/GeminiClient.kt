package com.example.englishlearningapp.data.remote.ai

import android.util.Log
import com.example.englishlearningapp.BuildConfig
import com.example.englishlearningapp.features.chat.model.Message
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.random.Random

class GeminiClient(
    private val apiKey: String = BuildConfig.GEMINI_API_KEY,
    private val modelNames: List<String> = DEFAULT_MODELS
) {
    private val models = modelNames.associateWith { modelName ->
        GenerativeModel(
            modelName = modelName,
            apiKey = apiKey
        )
    }

    suspend fun sendChatMessage(
        systemPrompt: String,
        userMessage: String,
        history: List<Message>
    ): Result<String> {
        return runWithFallback(operation = "chat") { model ->
            val activeChat = model.startChat(
                history = buildList {
                    add(content(role = "user") { text(systemPrompt) })
                    add(content(role = "model") { text("Understood! I'm ready to help.") })
                    history.forEach { msg ->
                        add(content(role = msg.role) { text(msg.content) })
                    }
                }
            )
            activeChat.sendMessage(userMessage).text ?: throw EmptyGeminiResponseException()
        }
    }

    suspend fun generateText(inputContent: Content): Result<String> {
        return runWithFallback(operation = "generateContent") { model ->
            model.generateContent(inputContent).text ?: throw EmptyGeminiResponseException()
        }
    }

    private suspend fun runWithFallback(
        operation: String,
        call: suspend (GenerativeModel) -> String
    ): Result<String> {
        var lastError: Exception? = null

        modelNames.forEachIndexed { modelIndex, modelName ->
            val model = models.getValue(modelName)
            repeat(MAX_ATTEMPTS_PER_MODEL) { attempt ->
                try {
                    return Result.success(call(model))
                } catch (e: Exception) {
                    lastError = e
                    val canRetry = e.isRetryableGeminiError()
                    val hasAnotherAttempt = attempt < MAX_ATTEMPTS_PER_MODEL - 1
                    val hasFallbackModel = modelIndex < modelNames.lastIndex

                    Log.w(
                        TAG,
                        "$operation failed on $modelName, attempt ${attempt + 1}: ${e.message}",
                        e
                    )

                    if (!canRetry || (!hasAnotherAttempt && !hasFallbackModel)) {
                        return Result.failure(e)
                    }

                    if (hasAnotherAttempt) {
                        delay(backoffDelayMillis(attempt))
                    }
                }
            }
        }

        return Result.failure(lastError ?: IllegalStateException("Gemini request failed"))
    }

    private fun backoffDelayMillis(attempt: Int): Long {
        val baseDelay = 700.0 * 2.0.pow(attempt.toDouble())
        val jitter = Random.nextLong(150L, 450L)
        return baseDelay.toLong() + jitter
    }

    private fun Exception.isRetryableGeminiError(): Boolean {
        val text = listOfNotNull(message, cause?.message)
            .joinToString(separator = " ")
            .lowercase()

        return RETRYABLE_MARKERS.any { marker -> marker in text }
    }

    private class EmptyGeminiResponseException : Exception("Empty response from Gemini")

    companion object {
        private const val TAG = "GeminiClient"
        private const val MAX_ATTEMPTS_PER_MODEL = 3

        private val DEFAULT_MODELS = listOf(
            "gemini-2.5-flash",
            "gemini-2.5-flash-lite"
        )

        private val RETRYABLE_MARKERS = listOf(
            "429",
            "500",
            "503",
            "504",
            "deadline_exceeded",
            "internal",
            "overloaded",
            "resource_exhausted",
            "service unavailable",
            "temporarily",
            "unavailable"
        )
    }
}
