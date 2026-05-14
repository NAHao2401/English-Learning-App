package com.example.englishlearningapp.data.repository

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
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun sendMessage(
        userMessage: String,
        history: List<Message>
    ): Result<String> {
        return try {
            // Build conversation history theo format Gemini yêu cầu
            val chat = model.startChat(
                history = history.map { msg ->
                    content(role = msg.role) { text(msg.content) }
                }
            )

            // Thêm system context vào message đầu tiên nếu history rỗng
            val prompt = if (history.isEmpty()) {
                "$systemPrompt\n\nUser: $userMessage"
            } else {
                userMessage
            }

            val response = chat.sendMessage(prompt)
            Result.success(response.text ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}