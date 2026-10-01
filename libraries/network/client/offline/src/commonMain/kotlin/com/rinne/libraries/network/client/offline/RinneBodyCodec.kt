package com.rinne.libraries.network.client.offline

import com.rinne.libraries.network.client.core.model.RinneContentType
import com.rinne.libraries.network.client.core.model.RinneOutgoingContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType

/**
 * Converts typed values to request bodies and response text back to typed values. Lives above the
 * transport so cached/queued bodies are plain text and the HTTP engine never needs to know about
 * serialization.
 */
interface RinneBodyCodec {
    fun encode(value: Any?, type: KType): RinneOutgoingContent

    fun decode(text: String, type: KType): Any?
}

@Suppress("UNCHECKED_CAST")
internal fun <T> RinneBodyCodec.decodeAs(text: String, type: KType): T = decode(text, type) as T

class RinneJsonBodyCodec(private val json: Json = DefaultJson) : RinneBodyCodec {

    override fun encode(value: Any?, type: KType): RinneOutgoingContent = when {
        value == null -> RinneOutgoingContent.Empty
        // Matches Ktor's ContentNegotiation: a String body is sent as-is.
        value is String -> RinneOutgoingContent.Text(value, contentType = RinneContentType.Text.Plain)
        else -> RinneOutgoingContent.Text(
            text = json.encodeToString(json.serializersModule.serializer(type), value),
            contentType = RinneContentType.Application.Json,
        )
    }

    override fun decode(text: String, type: KType): Any? = when (type.classifier) {
        Unit::class -> Unit
        // Matches Ktor's ContentNegotiation: a String response is the raw body.
        String::class -> text
        else -> json.decodeFromString(json.serializersModule.serializer(type), text)
    }

    companion object {
        val DefaultJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            encodeDefaults = false
        }
    }
}
