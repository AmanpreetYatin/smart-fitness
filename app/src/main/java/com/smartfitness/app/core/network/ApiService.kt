package com.smartfitness.app.core.network

import com.smartfitness.app.domain.model.ChatRequest
import com.smartfitness.app.domain.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("v1/chat/completions")
    suspend fun getChatResponse(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ): ChatResponse

}