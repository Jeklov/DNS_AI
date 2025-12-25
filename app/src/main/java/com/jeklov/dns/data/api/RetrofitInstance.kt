package com.jeklov.dns.data.api

import com.google.gson.GsonBuilder
import com.jeklov.dns.data.api.ai.chat.AIChatAPI
import com.jeklov.dns.data.api.ai.chat.deserialize.AIChatResponseDeserializer
import com.jeklov.dns.data.api.ai.chat.deserialize.AIProductObjectDeserializer
import com.jeklov.dns.data.api.ai.chat.models.AIChatResponse
import com.jeklov.dns.data.api.ai.chat.models.AIProductObject
import com.jeklov.dns.data.api.chat.ChatAPI
import com.jeklov.dns.data.api.ping.PingAPI
import com.jeklov.dns.data.api.product.ProductAPI
import com.jeklov.dns.data.api.product.deserialize.ProductDeserializer
import com.jeklov.dns.data.api.product.models.ProductObject
import com.jeklov.dns.data.util.Constants.Companion.BASE_URL
import com.jeklov.dns.data.util.Constants.Companion.tokenUser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {

        private val retrofitPing by lazy {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Логирование запросов и ответов
            }

            // Создаем Interceptor для добавления токена в заголовок
            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val requestWithToken = originalRequest.newBuilder()
                    .header("Ping", "Bearer $tokenUser") // Добавляем токен
                    .build()
                chain.proceed(requestWithToken)
            }

            // Настраиваем OkHttpClient с Interceptor'ами
            val client = OkHttpClient.Builder()
                .addInterceptor(logging) // Логирование
                .addInterceptor(authInterceptor) // Добавление токена
                .build()

            // Создаем Retrofit
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val pingAPI: PingAPI by lazy {
            retrofitPing.create(PingAPI::class.java)
        }

        private val retrofitChat by lazy {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Логирование запросов и ответов
            }

            // Создаем Interceptor для добавления токена в заголовок
            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val requestWithToken = originalRequest.newBuilder()
                    .header("Chat", "Bearer $tokenUser") // Добавляем токен
                    .build()
                chain.proceed(requestWithToken)
            }

            // Настраиваем OkHttpClient с Interceptor'ами
            val client = OkHttpClient.Builder()
                .addInterceptor(logging) // Логирование
                .addInterceptor(authInterceptor) // Добавление токена
                .build()

            // Создаем Retrofit
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val chatAPI: ChatAPI by lazy {
            retrofitChat.create(ChatAPI::class.java)
        }

        private val retrofitProduct by lazy {
            val gson = GsonBuilder()
                .registerTypeAdapter(ProductObject::class.java, ProductDeserializer())
                .create()

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Логирование запросов и ответов
            }

            // Создаем Interceptor для добавления токена в заголовок
            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val requestWithToken = originalRequest.newBuilder()
                    .header("Product", "Bearer $tokenUser") // Добавляем токен
                    .build()
                chain.proceed(requestWithToken)
            }

            // Настраиваем OkHttpClient с Interceptor'ами
            val client = OkHttpClient.Builder()
                .addInterceptor(logging) // Логирование
                .addInterceptor(authInterceptor) // Добавление токена
                .build()

            // Создаем Retrofit
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        val productAPI: ProductAPI by lazy {
            retrofitProduct.create(ProductAPI::class.java)
        }

        private val retrofitAIChat by lazy {
            // 1. Регистрируем наши кастомные десериализаторы для AI ответов
            val gson = GsonBuilder()
                .registerTypeAdapter(AIChatResponse::class.java, AIChatResponseDeserializer())
                .registerTypeAdapter(AIProductObject::class.java, AIProductObjectDeserializer())
                .create()

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val requestWithToken = originalRequest.newBuilder()
                    .header("AIChat", "Bearer $tokenUser")
                    .build()
                chain.proceed(requestWithToken)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(authInterceptor)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                // 2. Передаем настроенный gson с нашими парсерами
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        val aiChatAPI: AIChatAPI by lazy {
            // 3. Исправляем ошибку: здесь должно быть retrofitAIChat, а не retrofitProduct
            retrofitAIChat.create(AIChatAPI::class.java)
        }
    }
}