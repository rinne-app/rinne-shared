package com.rinne.shared.network.model.scopes

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/**
 * A scope the user made, as synced: what the apps show while it is active and in which language.
 * Writable fields: `name`, `description`, `language` (a language tag, `null` = the app's), `items`
 * (what it includes), `schedule` (when it switches on by itself, `null` = never set), `showInGlobal`,
 * `noteFolderId` (where notes made in it go). Global and the personal scope are built into the apps,
 * not synced.
 */
@Serializable
data class NetworkScope(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val description: String = "",
    val language: String? = null,
    val items: List<NetworkScopeItem> = emptyList(),
    val schedule: NetworkScopeSchedule? = null,
    val showInGlobal: Boolean = true,
    val noteFolderId: String? = null,
) {
    companion object {
        const val SyncType = "scope"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val Description = SyncField("description", String.serializer())
        val Language = SyncField("language", String.serializer().nullable)
        val Items = SyncField("items", ListSerializer(NetworkScopeItem.serializer()))
        val Schedule = SyncField("schedule", NetworkScopeSchedule.serializer().nullable)
        val ShowInGlobal = SyncField("showInGlobal", Boolean.serializer())
        val NoteFolderId = SyncField("noteFolderId", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Name, Description, Language, Items, Schedule, ShowInGlobal, NoteFolderId)
    }
}

/** One thing a scope includes: an item of an app, e.g. `notes` + a folder's id (with everything inside it). */
@Serializable
data class NetworkScopeItem(
    val app: String,
    val id: String,
) {
    companion object {
        /** A notes folder. */
        const val AppNotes = "notes"
    }
}

/**
 * When a scope switches on by itself: on [days] (1 = Monday … 7 = Sunday) from [startMinute] to
 * [endMinute] (minutes after midnight, start before end), while [enabled].
 */
@Serializable
data class NetworkScopeSchedule(
    val enabled: Boolean,
    val days: List<Int>,
    val startMinute: Int,
    val endMinute: Int,
)
