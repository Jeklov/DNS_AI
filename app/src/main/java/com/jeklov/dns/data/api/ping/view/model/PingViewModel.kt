package com.jeklov.dns.data.api.ping.view.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeklov.dns.data.api.ping.models.PingResponse
import com.jeklov.dns.data.api.ping.repository.PingRepository
import com.jeklov.dns.data.util.Constants.Companion.NO_INTERNET_CONNECTION
import com.jeklov.dns.data.util.Constants.Companion.NO_SIGNAL
import com.jeklov.dns.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow // Импорт 1
import kotlinx.coroutines.flow.StateFlow        // Импорт 2
import kotlinx.coroutines.flow.asStateFlow      // Импорт 3
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class PingViewModel(
    app: Application,
    private val repository: PingRepository
) : AndroidViewModel(app) {

    // 1. Создаем приватный MutableStateFlow с начальным значением null
    private val _pingToken = MutableStateFlow<Resource<PingResponse>?>(null)

    // 2. Открываем наружу неизменяемый StateFlow
    val pingToken: StateFlow<Resource<PingResponse>?> = _pingToken.asStateFlow()

    private var pingResponseToken: PingResponse? = null

    fun ping() =
        viewModelScope.launch {
            pingInternet()
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

    private fun pingResponse(response: Response<PingResponse>): Resource<PingResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pingResponseToken = resultResponse
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun pingInternet() {
        // 3. Обновляем значение через .value (вместо postValue)
        _pingToken.value = Resource.Loading
        try {
            if (internetConnection((this.getApplication()))) {
                val response = repository.ping()
                _pingToken.value = pingResponse(response)
            } else {
                _pingToken.value = Resource.Error(NO_INTERNET_CONNECTION)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _pingToken.value = Resource.Error(t.message.toString())
                else -> _pingToken.value = Resource.Error(NO_SIGNAL)
            }
        }
    }
}
