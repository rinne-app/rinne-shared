package com.rinne.shared.network.model.notes

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/**
 * A note as synced (`entity` of a sync change, field names of push mutations). Writable fields:
 * `title`, `content`, `folderId` (`null` = unfiled), `pinned` (pinned in its folder), `onHome` (pinned
 * to Home), `position` (its place in the user's manual order: a key compared as text, `null` = never
 * arranged).
 */
@Serializable
data class NetworkNote(
    val id: String,
    val folderId: String?,
    val title: String,
    val content: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val pinned: Boolean = false,
    val onHome: Boolean = false,
    val position: String? = null,
) {
    companion object {
        const val SyncType = "note"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Title = SyncField("title", String.serializer())
        val Content = SyncField("content", String.serializer())
        val FolderId = SyncField("folderId", String.serializer().nullable)
        val Pinned = SyncField("pinned", Boolean.serializer())
        val OnHome = SyncField("onHome", Boolean.serializer())
        val Position = SyncField("position", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Title, Content, FolderId, Pinned, OnHome, Position)
    }
}

/**
 * A note folder as synced. Writable fields: `name`, `parentId` (the folder it is in; `null` = top
 * level), `pinned`, `onHome`, `position` (as a note's). Deleting a folder moves its notes and
 * subfolders one level up.
 */
@Serializable
data class NetworkNoteFolder(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val parentId: String? = null,
    val pinned: Boolean = false,
    val onHome: Boolean = false,
    val position: String? = null,
) {
    companion object {
        const val SyncType = "note_folder"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val ParentId = SyncField("parentId", String.serializer().nullable)
        val Pinned = SyncField("pinned", Boolean.serializer())
        val OnHome = SyncField("onHome", Boolean.serializer())
        val Position = SyncField("position", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Name, ParentId, Pinned, OnHome, Position)
    }
}
