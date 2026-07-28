package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneHttpStatusCode
import io.ktor.http.HttpStatusCode

internal fun RinneHttpStatusCode.asKtor() = HttpStatusCode(value, description)

internal fun HttpStatusCode.asRinne() = RinneHttpStatusCode(value, description)
