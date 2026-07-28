package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneHttpMethod
import io.ktor.http.HttpMethod

internal fun RinneHttpMethod.asKtor() = when (this) {
    RinneHttpMethod.Get -> HttpMethod.Get
    RinneHttpMethod.Post -> HttpMethod.Post
    RinneHttpMethod.Put -> HttpMethod.Put
    RinneHttpMethod.Patch -> HttpMethod.Patch
    RinneHttpMethod.Delete -> HttpMethod.Delete
    RinneHttpMethod.Head -> HttpMethod.Head
    RinneHttpMethod.Options -> HttpMethod.Options
    is RinneHttpMethod.Custom -> HttpMethod.parse(value)
}

internal fun HttpMethod.asRinne() = when (this) {
    HttpMethod.Get -> RinneHttpMethod.Get
    HttpMethod.Post -> RinneHttpMethod.Post
    HttpMethod.Put -> RinneHttpMethod.Put
    HttpMethod.Patch -> RinneHttpMethod.Patch
    HttpMethod.Delete -> RinneHttpMethod.Delete
    HttpMethod.Head -> RinneHttpMethod.Head
    HttpMethod.Options -> RinneHttpMethod.Options
    else -> RinneHttpMethod.Custom(value)
}
