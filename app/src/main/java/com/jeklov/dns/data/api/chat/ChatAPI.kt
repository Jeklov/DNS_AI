package com.jeklov.dns.data.api.chat

import com.jeklov.dns.data.api.chat.models.ChatRequest
import com.jeklov.dns.data.api.chat.models.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatAPI {

    @POST("/chat/")
    suspend fun chat(
        @Body request: ChatRequest
    ): Response<ChatResponse>

}