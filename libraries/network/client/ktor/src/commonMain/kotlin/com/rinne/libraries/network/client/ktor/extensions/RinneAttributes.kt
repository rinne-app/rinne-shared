package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneAttributeKey
import com.rinne.libraries.network.client.core.model.RinneAttributes
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes

internal fun RinneAttributes.asKtor(): Attributes {
    val attributes = Attributes()
    forEach { (key, value) ->
        attributes.put(AttributeKey(key.name), value)
    }
    return attributes
}

internal fun Attributes.asRinne(): RinneAttributes {
    val entries = mutableMapOf<RinneAttributeKey<*>, Any>()

    allKeys.forEach { key ->
        @Suppress("UNCHECKED_CAST")
        val attributeKey = key as AttributeKey<Any>
        getOrNull(attributeKey)?.let { value ->
            entries[RinneAttributeKey<Any>(attributeKey.name)] = value
        }
    }

    return object : RinneAttributes, Map<RinneAttributeKey<*>, Any> by entries {}
}
