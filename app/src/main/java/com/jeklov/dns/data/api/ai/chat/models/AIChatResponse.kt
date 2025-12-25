package com.jeklov.dns.data.api.ai.chat.models

import com.google.gson.annotations.JsonAdapter
import com.jeklov.dns.data.api.ai.chat.deserialize.AIChatResponseDeserializer
import com.jeklov.dns.data.api.ai.chat.deserialize.AIProductObjectDeserializer
import com.jeklov.dns.data.user.AIChatResponseAdapter
import com.jeklov.dns.data.user.AIProductObjectAdapter

@JsonAdapter(AIChatResponseAdapter::class) // Points to the new adapter
data class AIChatResponse(
    val price: Int? = null,
    val type: String,
    val comment: String,
    val components: List<AIProductObject>
)

@JsonAdapter(AIProductObjectAdapter::class) // Points to the new adapter
data class AIProductObject(
    val id: Int,
    val price: Int,
    val model: String,
    val category: String? = null,
    val src: String,
    val details: Map<String, String>
)
