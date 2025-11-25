package com.jeklov.dns.data.api.product.deserialize

import com.jeklov.dns.data.api.product.models.ProductObject

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ProductDeserializer : JsonDeserializer<ProductObject> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ProductObject {
        val jsonObject = json.asJsonObject

        // 1. Извлекаем обязательные поля (с защитой от null и дефолтными значениями)
        // Ключи берем из вашего JSON (на русском)
        val price = jsonObject.get("Цена")?.asInt ?: 0
        val model = jsonObject.get("Модель")?.asString ?: "Unknown Model"
        val src = jsonObject.get("Изображение")?.asString ?: ""

        // 2. Собираем все остальные поля в Map
        val detailsMap = mutableMapOf<String, String>()

        // Список ключей, которые мы уже обработали и не хотим видеть в Map
        val excludedKeys = setOf("Цена", "Модель", "Изображение")

        for ((key, value) in jsonObject.entrySet()) {
            if (key !in excludedKeys) {
                // Превращаем любое значение (строку, число, boolean) в String для Map
                detailsMap[key] = value.asString
            }
        }

        return ProductObject(
            price = price,
            model = model,
            src = src,
            details = detailsMap
        )
    }
}
