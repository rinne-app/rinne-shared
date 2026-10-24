package com.rinne.shared.network.model.tags

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/**
 * A tag as synced. Writable fields: `name` (one segment of its path: `grammar` in
 * `#language/deutsch/grammar`), `parentId` (the tag it is nested in; `null` = top level), `pinned`
 * (shown in the apps' sidebars), `scopeId` (the scope it belongs to and shows in; `null` = none: it shows
 * in Global and the personal scope, which show every tag). The apps delete a tag together with its nested tags and their items;
 * the server keeps no references between tags and items.
 */
@Serializable
data class NetworkTag(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val parentId: String? = null,
    val pinned: Boolean = false,
    val scopeId: String? = null,
) {
    companion object {
        const val SyncType = "tag"

        /** What separates the names of a tag's path. */
        const val PathSeparator = '/'
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val ParentId = SyncField("parentId", String.serializer().nullable)
        val Pinned = SyncField("pinned", Boolean.serializer())
        val ScopeId = SyncField("scopeId", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Name, ParentId, Pinned, ScopeId)
    }
}

/**
 * One tag on one thing: the tag [tagId] on the entity [entityId] of the kind [kind] (an entity
 * reference's kind: `note`, `task`, `event` …). Writable fields: all three (merging tags re-points
 * `tagId`).
 */
@Serializable
data class NetworkTagItem(
    val id: String,
    val tagId: String,
    val kind: String,
    val entityId: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
) {
    companion object {
        const val SyncType = "tag_item"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val TagId = SyncField("tagId", String.serializer())
        val Kind = SyncField("kind", String.serializer())
        val EntityId = SyncField("entityId", String.serializer())

        val All: List<SyncField<*>> = listOf(TagId, Kind, EntityId)
    }
}
