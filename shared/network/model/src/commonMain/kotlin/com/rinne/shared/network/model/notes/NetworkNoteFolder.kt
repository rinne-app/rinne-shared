package com.rinne.shared.network.model.notes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A user-owned note folder. A null [parentId] is a root folder.
 *
 * `All notes` and `Unfiled` are client-side system views, not persisted folders.
 */
@Serializable
data class NetworkNoteFolder(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("parentId") val parentId: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("revision") val revision: Long,
)

@Serializable
data class NetworkCreateNoteFolderBody(
    @SerialName("name") val name: String,
    @SerialName("parentId") val parentId: String? = null,
)

/**
 * Partial folder update. Every field is optional; only the ones supplied are applied.
 *
 * A null [parentId] is ambiguous on its own — it is both "absent" and "move to the root" — so
 * [updateParent] carries the intent: the parent changes only when it is `true`, and moving a folder
 * to the root is `updateParent = true` with a null [parentId].
 */
@Serializable
data class NetworkUpdateNoteFolderBody(
    @SerialName("name") val name: String? = null,
    @SerialName("parentId") val parentId: String? = null,
    @SerialName("updateParent") val updateParent: Boolean = false,
)

/**
 * The outcome of an atomic folder deletion.
 *
 * Notes are never deleted with their folder: direct notes move to [destinationFolderId] (the
 * deleted folder's parent, or `Unfiled` when it was a root folder) and direct child folders are
 * promoted to that same parent.
 */
@Serializable
data class NetworkDeleteNoteFolderResult(
    @SerialName("deletedFolderId") val deletedFolderId: String,
    @SerialName("destinationFolderId") val destinationFolderId: String? = null,
    @SerialName("movedNoteIds") val movedNoteIds: List<String> = emptyList(),
    @SerialName("promotedFolderIds") val promotedFolderIds: List<String> = emptyList(),
)
