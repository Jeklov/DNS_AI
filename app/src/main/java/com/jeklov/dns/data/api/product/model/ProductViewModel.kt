package com.jeklov.dns.data.api.productList.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeklov.dns.data.api.product.models.ProductListRequest
// Импортируем вашу новую модель (убедитесь, что имя пакета совпадает с тем, где вы создали файл)
import com.jeklov.dns.data.api.product.models.ProductObject
import com.jeklov.dns.data.api.product.repository.ProductRepository
import com.jeklov.dns.data.util.Constants.Companion.NO_INTERNET_CONNECTION
import com.jeklov.dns.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ProductViewModel(
    app: Application,
    private val repository: ProductRepository
) : AndroidViewModel(app) {

    // 1. Теперь StateFlow хранит список объектов ProductObject
    private val _productListToken = MutableStateFlow<Resource<List<ProductObject>>?>(null)

    // 2. Открываем наружу неизменяемый StateFlow с новым типом
    val productListToken: StateFlow<Resource<List<ProductObject>>?> = _productListToken.asStateFlow()

    // Локальное хранилище тоже обновляем на список
    private var productListResponseToken: List<ProductObject>? = null

    fun productList(request: ProductListRequest) =
        viewModelScope.launch {
            productListInternet(
                request
            )
        }

    private fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    // Обновленная функция обработки ответа
    // Входящий тип: Response<List<ProductObject>>
    // Возвращаемый тип: Resource<List<ProductObject>>
    private fun productListResponse(response: Response<List<ProductObject>>): Resource<List<ProductObject>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                productListResponseToken = resultResponse
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun productListInternet(request: ProductListRequest) {
        _productListToken.value = Resource.Loading
        try {
            if (internetConnection((this.getApplication()))) {
                // Здесь предполагается, что repository.productList(request) 
                // теперь возвращает Response<List<ProductObject>>
                val response = repository.productList(request)
                _productListToken.value = productListResponse(response)
            } else {
                _productListToken.value = Resource.Error(NO_INTERNET_CONNECTION)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _productListToken.value = Resource.Error(t.message.toString())
                // Здесь лучше выводить реальную ошибку, чтобы понимать, что идет не так
                else -> _productListToken.value = Resource.Error("Error: ${t.message}")
            }
        }
    }
}
