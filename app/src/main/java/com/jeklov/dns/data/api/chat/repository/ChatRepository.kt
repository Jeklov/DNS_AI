package com.jeklov.dns.data.api.chat.repository

import com.jeklov.dns.data.api.RetrofitInstance
import com.jeklov.dns.data.api.chat.models.ChatRequest

class ChatRepository {

    suspend fun chat(request: ChatRequest) =
        RetrofitInstance.chatAPI.chat(request)

}