package com.rinne.shared.network.model.notes

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/**
 * A note as synced (`entity` of a sync change, field names of push mutations). Writable fields:
 * `title`, `content`, `folderId` (`null` = unfiled).
 */
@Serializable
data class NetworkNote(
    val id: String,
    val folderId: String?,
    val title: String,
    val content: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
) {
    companion object {
        const val SyncType = "note"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Title = SyncField("title", String.serializer())
        val Content = SyncField("content", String.serializer())
        val FolderId = SyncField("folderId", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Title, Content, FolderId)
    }
}

/**
 * A note folder as synced. Writable fields: `name`, `parentId` (the folder it is in; `null` = top
 * level). Deleting a folder moves its notes and subfolders one level up.
 */
@Serializable
data class NetworkNoteFolder(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val parentId: String? = null,
) {
    companion object {
        const val SyncType = "note_folder"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val ParentId = SyncField("parentId", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Name, ParentId)
    }
}
