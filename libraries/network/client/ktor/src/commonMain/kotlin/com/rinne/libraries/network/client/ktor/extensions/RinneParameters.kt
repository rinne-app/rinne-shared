package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneParameters
import io.ktor.http.Parameters

internal fun RinneParameters.asKtor() = if (entries.isEmpty()) {
    Parameters.Empty
} else {
    Parameters.build {
        entries.forEach { (name, values) ->
            values.forEach { value ->
                append(name, value)
            }
        }
    }
}

internal fun Parameters.asRinne() = if (isEmpty()) {
    RinneParameters.Empty
} else {
    RinneParameters(
        entries = names().associateWith { name ->
            getAll(name).orEmpty()
        }
    )
}
