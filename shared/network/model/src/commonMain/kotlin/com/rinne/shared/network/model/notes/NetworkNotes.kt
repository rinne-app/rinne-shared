package com.rinne.shared.network.model.notes

import com.rinne.libraries.date.time.core.RinneTimestamp
import kotlinx.serialization.Serializable

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
}

/** A note folder as synced. Writable fields: `name`. */
@Serializable
data class NetworkNoteFolder(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
) {
    companion object {
        const val SyncType = "note_folder"
    }
}
