package com.jeklov.dns.data.api.ai.chat

import com.jeklov.dns.data.api.ai.chat.models.AIChatResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AIChatAPI {

    @GET("/ai/chat/")
    suspend fun aiChat(
        @Query("user_input") userInput: String
    ): Response<AIChatResponse>

}