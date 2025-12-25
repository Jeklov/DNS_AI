package com.jeklov.dns.data.api.ai.aiChat.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeklov.dns.data.api.ai.chat.models.AIChatRequest
import com.jeklov.dns.data.api.ai.chat.models.AIChatResponse
import com.jeklov.dns.data.api.ai.chat.repository.AIChatRepository
import com.jeklov.dns.data.util.Constants.Companion.NO_INTERNET_CONNECTION
import com.jeklov.dns.data.util.Constants.Companion.NO_SIGNAL
import com.jeklov.dns.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow // Импорт 1
import kotlinx.coroutines.flow.StateFlow        // Импорт 2
import kotlinx.coroutines.flow.asStateFlow      // Импорт 3
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class AIChatViewModel(
    app: Application,
    private val repository: AIChatRepository
) : AndroidViewModel(app) {

    // 1. Создаем приватный MutableStateFlow с начальным значением null
    private val _aiChatToken = MutableStateFlow<Resource<AIChatResponse>?>(null)

    // 2. Открываем наружу неизменяемый StateFlow
    val aiChatToken: StateFlow<Resource<AIChatResponse>?> = _aiChatToken.asStateFlow()

    private var aiChatResponseToken: AIChatResponse? = null

    fun aiChat(request: AIChatRequest) =
        viewModelScope.launch {
            aiChatInternet(
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

    private fun aiChatResponse(response: Response<AIChatResponse>): Resource<AIChatResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                aiChatResponseToken = resultResponse
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun aiChatInternet(request: AIChatRequest) {
        // 3. Обновляем значение через .value (вместо postValue)
        _aiChatToken.value = Resource.Loading
        try {
            if (internetConnection((this.getApplication()))) {
                val response = repository.aiChat(request)
                _aiChatToken.value = aiChatResponse(response)
            } else {
                _aiChatToken.value = Resource.Error(NO_INTERNET_CONNECTION)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _aiChatToken.value = Resource.Error(t.message.toString())
                else -> _aiChatToken.value = Resource.Error(NO_SIGNAL)
            }
        }
    }
}
