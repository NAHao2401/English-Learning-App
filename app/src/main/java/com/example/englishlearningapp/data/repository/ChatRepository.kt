package com.example.englishlearningapp.data.repository

import android.util.Log
import com.example.englishlearningapp.features.chat.model.Message
import com.example.englishlearningapp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class ChatRepository {

    private val systemPrompt = """
        You are an English tutor helping Vietnamese learners practice English.
        - Keep responses short and clear (2-4 sentences max)
        - Gently correct grammar mistakes if the user makes any
        - If asked in Vietnamese, reply in both Vietnamese and English
        - Encourage the user to try speaking/writing in English
    """.trimIndent()

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun sendMessage(
        userMessage: String,
        history: List<Message>
    ): Result<String> {
        return try {
            val activeChat = model.startChat(
                history = buildList {
                    add(content(role = "user") { text(systemPrompt) })
                    add(content(role = "model") { text("Understood! I'm ready to help.") })
                    history.forEach { msg ->
                        add(content(role = msg.role) { text(msg.content) })
                    }
                }
            )
            val response = activeChat.sendMessage(userMessage)
            Result.success(response.text ?: "No response")
        } catch (e: Exception) {
            Log.e("ChatRepo", "Error: ${e.message}", e)
            Result.failure(e)
        }
    }
}