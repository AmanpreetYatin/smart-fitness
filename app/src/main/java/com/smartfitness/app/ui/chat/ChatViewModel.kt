package com.smartfitness.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfitness.app.core.snackbar.SnackbarController
import com.smartfitness.app.domain.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository  // ✅ Hilt injects @Singleton ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    fun sendMessage(userMessage: String) {
        //if you want to add previous history of conversation, you can modify the prompt to include previous messages
        val prompt = """
               You are a professional fitness coach.
               Give short, practical, and actionable advice.
               Focus on workouts, diet, and health.         
               User: $userMessage
             """
        val userMsg = Message(role = "user", content = userMessage)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            _isTyping.value = true
            try {
                val reply = chatRepository.sendMessage(prompt)
                _messages.value = _messages.value + Message(role = "assistant", content = reply)
            } catch (e: Exception) {
                SnackbarController.showError("Failed to get response: ${e.message}")
                e.printStackTrace()
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun streamResponse(userMessage: String) = flow {
        val fullText = "Here is a workout plan tailored for you 💪"

        var current = ""

        fullText.forEach { char ->
            current += char
            emit(current)
            delay(30) // typing speed
        }
    }

}