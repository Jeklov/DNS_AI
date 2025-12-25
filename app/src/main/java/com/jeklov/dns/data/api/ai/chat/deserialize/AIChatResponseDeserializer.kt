package com.jeklov.dns.data.api.ai.chat.deserialize

import com.jeklov.dns.data.api.product.models.ProductObject

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.jeklov.dns.data.api.ai.chat.models.AIChatResponse
import com.jeklov.dns.data.api.ai.chat.models.AIProductObject
import java.lang.reflect.Type

class AIChatResponseDeserializer : JsonDeserializer<AIChatResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): AIChatResponse {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString

        var price: Int? = null
        var comment = ""
        val components = ArrayList<AIProductObject>()

        if (type == "pc_build_ready") {
            // Логика для первого JSON
            if (jsonObject.has("total_price")) {
                price = jsonObject.get("total_price").asInt
            }
            if (jsonObject.has("comment")) {
                comment = jsonObject.get("comment").asString
            }
            if (jsonObject.has("components")) {
                val compsArray = jsonObject.getAsJsonArray("components")
                compsArray.forEach { element ->
                    components.add(context.deserialize(element, AIProductObject::class.java))
                }
            }
        } else if (type == "search_result") {
            // Логика для второго JSON (search_result)
            val content = jsonObject.getAsJsonObject("content")

            if (content.has("title")) {
                // Используем title как comment для единообразия
                comment = content.get("title").asString
            }

            if (content.has("items")) {
                val itemsArray = content.getAsJsonArray("items")
                itemsArray.forEach { element ->
                    components.add(context.deserialize(element, AIProductObject::class.java))
                }
            }
        }

        return AIChatResponse(
            price = price,
            type = type,
            comment = comment,
            components = components
        )
    }
}

/**
 * Десериализатор для продукта.
 * Вытаскивает конкретные поля (id, Цена, Модель...), а все остальное складывает в map 'details'.
 */
class AIProductObjectDeserializer : JsonDeserializer<AIProductObject> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): AIProductObject {
        val jsonObject = json.asJsonObject

        // Основные поля, которые мы ищем явно
        var id = 0
        var price = 0
        var model = ""
        var category: String? = null
        var src = ""

        val details = HashMap<String, String>()

        // Проходимся по всем полям JSON объекта
        for ((key, value) in jsonObject.entrySet()) {
            // Пропускаем null значения
            if (value.isJsonNull) continue

            val stringValue = if (value.isJsonPrimitive) value.asString else value.toString()

            when (key) {
                "id" -> id = value.asInt
                "Цена" -> price = value.asInt // Мапим "Цена" в поле price
                "Модель" -> model = stringValue // Мапим "Модель" в поле model
                "category" -> category = stringValue
                "Изображение" -> src = stringValue // Мапим "Изображение" в src
                // Исключаем поля, которые не нужны в details, но и не входят в основные (если такие есть)
                else -> {
                    // Всё остальное (Производитель, Ядра, Тайминги и т.д.) кладем в details
                    details[key] = stringValue
                }
            }
        }

        return AIProductObject(
            id = id,
            price = price,
            model = model,
            category = category,
            src = src,
            details = details
        )
    }
}