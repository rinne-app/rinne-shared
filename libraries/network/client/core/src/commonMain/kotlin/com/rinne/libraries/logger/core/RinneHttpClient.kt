package com.rinne.libraries.logger.core

import io.ktor.client.HttpClient
import io.ktor.client.request.get

interface RinneHttpClient {
    suspend fun get()
}

class RinneHttpClientDefault(private val httpClient: RinneHttpClient) {
}