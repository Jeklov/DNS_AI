package com.jeklov.dns.data.api.product.repository

import com.jeklov.dns.data.api.RetrofitInstance
import com.jeklov.dns.data.api.product.models.ProductListRequest

class ProductRepository {

    suspend fun productList(request: ProductListRequest) =
        RetrofitInstance.productAPI.product(request.category_name)

}