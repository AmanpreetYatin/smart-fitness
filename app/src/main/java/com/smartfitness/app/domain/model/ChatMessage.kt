package com.smartfitness.app.domain.model

import android.net.Uri

data class ChatMessage(
    val text: String = "",
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: Uri? = null,
    val status: MessageStatus = MessageStatus.SENT
)