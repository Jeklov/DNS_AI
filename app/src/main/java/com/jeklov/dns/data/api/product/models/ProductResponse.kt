package com.jeklov.dns.data.api.product.models

data class ProductListResponse(
    val response: ArrayList<String>
)

data class ProductObject(
    val price: Int,
    val model: String,
    val src: String,
    val details: Map<String, String> // Все остальные поля попадут сюда
)