package com.rinne.libraries.network.client.core.model

sealed interface RinneOutgoingContent {

    fun isEmpty(): Boolean = this == Empty

    object Empty : RinneOutgoingContent

    data class Serializable<T>(val data: T) : RinneOutgoingContent

    data class Bytes(
        val bytes: ByteArray,
        val contentType: RinneContentType? = null,
        val headers: RinneHttpHeaders = RinneHttpHeaders.Empty
    ) : RinneOutgoingContent

    data class Text(
        val text: String,
        val charset: RinneCharset = RinneCharset.UTF_8,
        val contentType: RinneContentType? = null,
        val headers: RinneHttpHeaders = RinneHttpHeaders.Empty
    ) : RinneOutgoingContent
}
