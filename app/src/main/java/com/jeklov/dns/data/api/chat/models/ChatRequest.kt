package com.jeklov.dns.data.api.chat.models

data class ChatRequest(
    val prompt: String,
    val max_tokens: Int = 1024,
)