package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneOutgoingContent
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.charset
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

internal fun RinneOutgoingContent.asKtor(): OutgoingContent = when (this) {
    RinneOutgoingContent.Empty -> RinneNoContent

    is RinneOutgoingContent.Serializable<*> -> RinneTextBodyContent(
        text = serializeWithKotlinx(data),
        contentType = ContentType.Application.Json
    )

    is RinneOutgoingContent.Bytes -> RinneByteArrayContent(
        bytes = bytes,
        contentType = contentType?.asKtor(),
        headers = headers.asKtor()
    )

    is RinneOutgoingContent.Text -> RinneTextBodyContent(
        text = text,
        charset = charset.asKtor(),
        contentType = contentType?.asKtor(),
        headers = headers.asKtor()
    )
}

internal fun OutgoingContent.asRinne() = when (this) {
    is OutgoingContent.NoContent -> RinneOutgoingContent.Empty

    is TextContent -> RinneOutgoingContent.Text(
        text = text,
        charset = (contentType.charset() ?: Charsets.UTF_8).asRinne(),
        contentType = contentType.withoutParameters().asRinne(),
        headers = headers.asRinne()
    )

    is OutgoingContent.ByteArrayContent -> RinneOutgoingContent.Bytes(
        bytes = bytes(),
        contentType = contentType?.withoutParameters()?.asRinne(),
        headers = headers.asRinne()
    )

    else -> RinneOutgoingContent.Serializable(this)
}

private object RinneNoContent : OutgoingContent.NoContent()

private class RinneByteArrayContent(
    private val bytes: ByteArray,
    override val contentType: ContentType? = null,
    override val headers: Headers = Headers.Empty
) : OutgoingContent.ByteArrayContent() {
    override val contentLength: Long get() = bytes.size.toLong()

    override fun bytes(): ByteArray = bytes
}

private class RinneTextBodyContent(
    private val text: String,
    private val charset: Charset = Charsets.UTF_8,
    contentType: ContentType? = null,
    override val headers: Headers = Headers.Empty
) : OutgoingContent.ByteArrayContent() {
    override val contentType: ContentType = (contentType ?: ContentType.Text.Plain).withCharset(charset)

    private val bytes = text.toByteArray(charset)

    override val contentLength: Long get() = bytes.size.toLong()

    override fun bytes(): ByteArray = bytes
}

private val rinneOutgoingContentJson = Json {
    explicitNulls = false
}

private fun serializeWithKotlinx(value: Any?): String {
    if (value == null) return "null"

    @Suppress("UNCHECKED_CAST")
    val serializer = try {
        findSerializer(value) as KSerializer<Any>
    } catch (e: SerializationException) {
        throw IllegalArgumentException(
            "RinneOutgoingContent.Serializable requires @Serializable payload: ${value::class}",
            e
        )
    }

    return rinneOutgoingContentJson.encodeToString(serializer, value)
}

@OptIn(InternalSerializationApi::class)
private fun findSerializer(value: Any): KSerializer<*> = value::class.serializer()
