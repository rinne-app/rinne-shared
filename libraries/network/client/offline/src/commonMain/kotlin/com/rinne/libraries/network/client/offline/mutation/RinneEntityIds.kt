package com.rinne.libraries.network.client.offline.mutation

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * Reads the id of a created entity from a response: `{"id": ...}` (the whole entity), a JSON string,
 * or a plain-text id.
 */
internal fun extractEntityId(body: String?, idField: String): String? {
    val text = body?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    return when (val element = runCatching { Json.parseToJsonElement(text) }.getOrNull()) {
        is JsonObject -> element[idField]?.let { runCatching { it.jsonPrimitive.content }.getOrNull() }
        is JsonPrimitive -> element.content
        null -> text
        else -> null
    }
}
