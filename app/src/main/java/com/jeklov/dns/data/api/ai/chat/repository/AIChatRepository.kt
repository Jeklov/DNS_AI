package com.jeklov.dns.data.api.ai.chat.repository

import com.jeklov.dns.data.api.RetrofitInstance
import com.jeklov.dns.data.api.ai.chat.models.AIChatRequest
class AIChatRepository {

    suspend fun aiChat(request: AIChatRequest) =
        RetrofitInstance. aiChatAPI.aiChat(request.mode.prompt + request.prompt)

}