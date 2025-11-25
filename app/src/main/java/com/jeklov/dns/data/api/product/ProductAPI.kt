package com.jeklov.dns.data.api.product

import com.jeklov.dns.data.api.product.models.ProductObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductAPI {

    @GET("/products/{category_name}")
    suspend fun product(
        @Path("category_name") categoryName: String
    ): Response<List<ProductObject>>

}