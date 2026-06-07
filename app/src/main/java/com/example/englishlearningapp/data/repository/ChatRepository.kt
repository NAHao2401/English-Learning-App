package com.example.englishlearningapp.data.repository

import android.util.Log
import com.example.englishlearningapp.data.remote.ai.GeminiClient
import com.example.englishlearningapp.features.chat.model.Message

class ChatRepository(
    private val geminiClient: GeminiClient = GeminiClient()
) {

    private val systemPrompt = """
        You are an English tutor helping Vietnamese learners practice English.
        - Keep responses short and clear (2-4 sentences max)
        - Gently correct grammar mistakes if the user makes any
        - If asked in Vietnamese, reply in both Vietnamese and English
        - Encourage the user to try speaking/writing in English
    """.trimIndent()

    suspend fun sendMessage(
        userMessage: String,
        history: List<Message>
    ): Result<String> {
        return try {
            geminiClient.sendChatMessage(
                systemPrompt = systemPrompt,
                userMessage = userMessage,
                history = history
            )
        } catch (e: Exception) {
            Log.e("ChatRepo", "Error: ${e.message}", e)
            Result.failure(e)
        }
    }
}
