package com.example.englishlearningapp.features.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.data.repository.ChatRepository
import com.example.englishlearningapp.features.chat.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Giữ lịch sử để gửi lên API mỗi lần
    private val conversationHistory = mutableListOf<Message>()

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isLoading) return

        val userMsg = Message(role = "user", content = text)
        conversationHistory.add(userMsg)

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg,
                inputText = "",
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            chatRepository.sendMessage(text, conversationHistory.dropLast(1))
                .fold(
                    onSuccess = { reply ->
                        val aiMsg = Message(role = "model", content = reply)
                        conversationHistory.add(aiMsg)
                        _uiState.update { state ->
                            state.copy(
                                messages = state.messages + aiMsg,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        conversationHistory.removeAt(conversationHistory.lastIndex) // rollback
                        _uiState.update { state ->
                            state.copy(
                                messages = state.messages.dropLast(1),
                                isLoading = false,
                                errorMessage = "Không gửi được tin nhắn. Thử lại?"
                            )
                        }
                    }
                )
        }
    }

    fun sendQuickPrompt(prompt: String) {
        _uiState.update { it.copy(inputText = prompt) }
        sendMessage()
    }
}