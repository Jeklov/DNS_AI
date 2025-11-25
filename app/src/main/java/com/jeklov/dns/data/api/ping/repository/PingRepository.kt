package com.jeklov.dns.data.api.ping.repository

import com.jeklov.dns.data.api.RetrofitInstance

class PingRepository {

    suspend fun ping() =
        RetrofitInstance.pingAPI.ping()

}