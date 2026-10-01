package com.rinne.libraries.network.client.offline

import com.rinne.libraries.network.client.core.model.RinneContentType
import com.rinne.libraries.network.client.core.model.RinneHttpMethod

internal fun RinneHttpMethod.asMethodName(): String = when (this) {
    RinneHttpMethod.Get -> "GET"
    RinneHttpMethod.Post -> "POST"
    RinneHttpMethod.Put -> "PUT"
    RinneHttpMethod.Patch -> "PATCH"
    RinneHttpMethod.Delete -> "DELETE"
    RinneHttpMethod.Head -> "HEAD"
    RinneHttpMethod.Options -> "OPTIONS"
    is RinneHttpMethod.Custom -> value.uppercase()
}

internal fun rinneHttpMethodOf(name: String): RinneHttpMethod = when (name.uppercase()) {
    "GET" -> RinneHttpMethod.Get
    "POST" -> RinneHttpMethod.Post
    "PUT" -> RinneHttpMethod.Put
    "PATCH" -> RinneHttpMethod.Patch
    "DELETE" -> RinneHttpMethod.Delete
    "HEAD" -> RinneHttpMethod.Head
    "OPTIONS" -> RinneHttpMethod.Options
    else -> RinneHttpMethod.Custom(name)
}

/** Content types a queued body can be persisted with; anything else is stored as a custom type. */
internal fun RinneContentType.asMimeType(): String = when (this) {
    RinneContentType.Application.Json -> "application/json"
    RinneContentType.Text.Plain -> "text/plain"
    is RinneContentType.Custom -> "$contentType/$contentSubtype"
    else -> "application/octet-stream"
}

internal fun rinneContentTypeOf(mimeType: String): RinneContentType = when (mimeType) {
    "application/json" -> RinneContentType.Application.Json
    "text/plain" -> RinneContentType.Text.Plain
    else -> mimeType.split('/', limit = 2).let { RinneContentType.Custom(it[0], it.getOrElse(1) { "*" }) }
}
