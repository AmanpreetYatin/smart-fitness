package com.smartfitness.app.ui.chat

import com.google.ai.client.generativeai.GenerativeModel
import com.smartfitness.app.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor() {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey    = BuildConfig.GEMINI_API_KEY
    )

    suspend fun sendMessage(prompt: String): String {
        return try {
            val response = model.generateContent(prompt)
            response.text ?: "No response"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }
}