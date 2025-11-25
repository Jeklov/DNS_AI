package com.jeklov.dns.data.api.ping

import com.jeklov.dns.data.api.ping.models.PingResponse
import retrofit2.Response
import retrofit2.http.GET

interface PingAPI {

    @GET("/")
    suspend fun ping(): Response<PingResponse>

}