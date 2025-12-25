package com.jeklov.dns.data.user

import com.google.gson.*
import com.jeklov.dns.data.api.ai.chat.models.AIChatResponse
import com.jeklov.dns.data.api.ai.chat.models.AIProductObject
import java.lang.reflect.Type

// --- Adapter for AIProductObject ---
class AIProductObjectAdapter : JsonSerializer<AIProductObject>, JsonDeserializer<AIProductObject> {

    // Serialization (Saving): Object -> JSON
    override fun serialize(src: AIProductObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        // Add standard fields
        jsonObject.addProperty("id", src.id)
        jsonObject.addProperty("price", src.price)
        jsonObject.addProperty("model", src.model)
        jsonObject.addProperty("category", src.category)
        jsonObject.addProperty("src", src.src)

        // Flatten the 'details' map back into the main JSON object
        src.details.forEach { (key, value) ->
            jsonObject.addProperty(key, value)
        }

        return jsonObject
    }

    // Deserialization (Loading): JSON -> Object
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): AIProductObject {
        val jsonObject = json.asJsonObject

        // Extract known fields
        val id = jsonObject.get("id")?.asInt ?: 0
        val price = jsonObject.get("price")?.asInt ?: 0
        val model = jsonObject.get("model")?.asString ?: ""
        val category = if (jsonObject.has("category")) jsonObject.get("category").asString else null
        val src = jsonObject.get("src")?.asString ?: ""

        // Put everything else into 'details'
        val details = mutableMapOf<String, String>()
        val knownKeys = setOf("id", "price", "model", "category", "src")

        jsonObject.entrySet().forEach { entry ->
            if (entry.key !in knownKeys) {
                // Convert all unknown values to String to be safe
                details[entry.key] = entry.value.toString().replace("\"", "")
            }
        }

        return AIProductObject(id, price, model, category, src, details)
    }
}

// --- Adapter for AIChatResponse ---
class AIChatResponseAdapter : JsonSerializer<AIChatResponse>, JsonDeserializer<AIChatResponse> {

    override fun serialize(src: AIChatResponse, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("price", src.price)
        jsonObject.addProperty("type", src.type)
        jsonObject.addProperty("comment", src.comment)

        // Serialize the list of components using the context
        jsonObject.add("components", context.serialize(src.components))

        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): AIChatResponse {
        val jsonObject = json.asJsonObject

        val price = if (jsonObject.has("price") && !jsonObject.get("price").isJsonNull) jsonObject.get("price").asInt else null
        val type = jsonObject.get("type")?.asString ?: ""
        val comment = jsonObject.get("comment")?.asString ?: ""

        // Deserialize the list
        val componentsArray = jsonObject.getAsJsonArray("components")
        val components = mutableListOf<AIProductObject>()

        componentsArray?.forEach { element ->
            val product = context.deserialize<AIProductObject>(element, AIProductObject::class.java)
            components.add(product)
        }

        return AIChatResponse(price, type, comment, components)
    }
}
