package com.rinne.libraries.network.client.core.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

sealed interface RinneIncomingContent {

    fun isEmpty(): Boolean = this == Empty

    data object Empty : RinneIncomingContent

    data class Bytes(
        val bytes: ByteArray,
        val contentType: RinneContentType? = null,
    ) : RinneIncomingContent

    data class Text(
        val text: String,
        val charset: RinneCharset = RinneCharset.UTF_8,
        val contentType: RinneContentType? = null,
    ) : RinneIncomingContent
}

val rinneIncomingContentJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

fun RinneIncomingContent.asTextOrNull() = when (this) {
    RinneIncomingContent.Empty -> null
    is RinneIncomingContent.Text -> text
    is RinneIncomingContent.Bytes -> bytes.decodeToString()
}

inline fun <reified T> RinneIncomingContent.decodeFromJson(json: Json = rinneIncomingContentJson): T {
    return when (this) {
        RinneIncomingContent.Empty -> error("Body is empty")
        is RinneIncomingContent.Text -> json.decodeFromString(text)
        is RinneIncomingContent.Bytes -> json.decodeFromString(bytes.decodeToString())
    }
}

inline fun <reified T> RinneIncomingContent.decodeFromJsonOrNull(json: Json = rinneIncomingContentJson): T? {
    return runCatching { decodeFromJson<T>(json) }.getOrNull()
}
