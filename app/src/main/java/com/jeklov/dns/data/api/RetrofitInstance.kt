package com.jeklov.dns.data.api

import com.google.gson.GsonBuilder
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
    }
}