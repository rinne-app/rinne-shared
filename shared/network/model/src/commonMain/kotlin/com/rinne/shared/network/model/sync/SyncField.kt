package com.rinne.shared.network.model.sync

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * One writable field of a synced entity: its wire name (a property of the entity DTO) and type.
 * The backend reads push `fields` and the client writes them through the same descriptors, so a
 * renamed or retyped field fails to compile on both sides.
 */
class SyncField<T>(val name: String, val serializer: KSerializer<T>) {
    override fun toString(): String = name
}

/** Builds the `fields` of a push mutation: `syncFields { set(NetworkNote.Fields.Title, "Plan") }`. */
fun syncFields(block: SyncFieldsBuilder.() -> Unit): JsonObject = SyncFieldsBuilder().apply(block).build()

class SyncFieldsBuilder internal constructor() {
    private val values = linkedMapOf<String, JsonElement>()

    fun <T> set(field: SyncField<T>, value: T) {
        values[field.name] = Json.encodeToJsonElement(field.serializer, value)
    }

    internal fun build(): JsonObject = JsonObject(values)
}

/** The value of [field] in [fields]: absent → [Absent]; present → decoded, or a failure. */
fun <T> JsonObject.readSyncField(field: SyncField<T>): SyncFieldValue<T> {
    val element = this[field.name] ?: return SyncFieldValue.Absent
    // A wrong JSON shape fails in different ways (SerializationException, index errors): all invalid.
    return runCatching { Json.decodeFromJsonElement(field.serializer, element) }.fold(
        onSuccess = { SyncFieldValue.Present(it) },
        onFailure = { SyncFieldValue.Invalid(it.message.orEmpty()) },
    )
}

sealed interface SyncFieldValue<out T> {
    data object Absent : SyncFieldValue<Nothing>
    data class Present<T>(val value: T) : SyncFieldValue<T>
    data class Invalid(val reason: String) : SyncFieldValue<Nothing>
}
