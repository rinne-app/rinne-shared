package com.rinne.libraries.content.generator.core.impl.ai

import com.rinne.libraries.content.generator.core.impl.RineContentGeneratorLogger
import com.rinne.libraries.logger.core.extensions.i
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol.Companion.HTTPS
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.minutes

interface RinneAiChatNetworkProvider {
    val httpClient: HttpClient
}

internal class RinneAiChatNetworkProviderImpl() : RinneAiChatNetworkProvider {

    val json = Json {
        isLenient = true
        prettyPrint = true
        encodeDefaults = false
        ignoreUnknownKeys = true
        useAlternativeNames = true
        explicitNulls = false
    }


    override val httpClient =
        HttpClient {
            defaultRequest {
                url { protocol = HTTPS }
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
            }
//
//            HttpResponseValidator {
//                validateResponse { response ->
////                errorManager.handleHTTPResponse(response)
//                }
//
//            }

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 10.minutes.inWholeMilliseconds
                requestTimeoutMillis = 10.minutes.inWholeMilliseconds
                connectTimeoutMillis = 10.minutes.inWholeMilliseconds
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) = RineContentGeneratorLogger.i(message = message)
                }
            }
        }
}