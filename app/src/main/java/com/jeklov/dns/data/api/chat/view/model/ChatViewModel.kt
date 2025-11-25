package com.jeklov.dns.data.api.chat.view.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeklov.dns.data.api.chat.models.ChatRequest
import com.jeklov.dns.data.api.chat.models.ChatResponse
import com.jeklov.dns.data.api.chat.repository.ChatRepository
import com.jeklov.dns.data.util.Constants.Companion.NO_INTERNET_CONNECTION
import com.jeklov.dns.data.util.Constants.Companion.NO_SIGNAL
import com.jeklov.dns.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow // Импорт 1
import kotlinx.coroutines.flow.StateFlow        // Импорт 2
import kotlinx.coroutines.flow.asStateFlow      // Импорт 3
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ChatViewModel(
    app: Application,
    private val repository: ChatRepository
) : AndroidViewModel(app) {

    // 1. Создаем приватный MutableStateFlow с начальным значением null
    private val _chatToken = MutableStateFlow<Resource<ChatResponse>?>(null)

    // 2. Открываем наружу неизменяемый StateFlow
    val chatToken: StateFlow<Resource<ChatResponse>?> = _chatToken.asStateFlow()

    private var chatResponseToken: ChatResponse? = null

    fun chat(request: ChatRequest) =
        viewModelScope.launch {
            chatInternet(
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

    private fun chatResponse(response: Response<ChatResponse>): Resource<ChatResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                chatResponseToken = resultResponse
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun chatInternet(request: ChatRequest) {
        // 3. Обновляем значение через .value (вместо postValue)
        _chatToken.value = Resource.Loading
        try {
            if (internetConnection((this.getApplication()))) {
                val response = repository.chat(request)
                _chatToken.value = chatResponse(response)
            } else {
                _chatToken.value = Resource.Error(NO_INTERNET_CONNECTION)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _chatToken.value = Resource.Error(t.message.toString())
                else -> _chatToken.value = Resource.Error(NO_SIGNAL)
            }
        }
    }
}
