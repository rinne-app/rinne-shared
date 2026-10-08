package com.rinne.shared.network.model.notes

import com.rinne.libraries.date.time.core.RinneTimestamp
import kotlinx.serialization.Serializable

@Serializable
data class NetworkNote(
    val id: String,
    val folderId: String?,
    val title: String,
    val content: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
)

/**
 * `GET /api/v1/notes?folder=<id|unfiled>&limit=<1..100>&cursor=<nextCursor>`, newest first.
 * [nextCursor] is opaque; pass it back unchanged to get the next page. `null` means no more pages.
 */
@Serializable
data class NetworkNotesPage(
    val items: List<NetworkNote>,
    val nextCursor: String?,
)

/** `POST /api/v1/notes`. A `null` [folderId] creates an unfiled note. */
@Serializable
data class NetworkCreateNoteRequest(
    val title: String,
    val content: String,
    val folderId: String? = null,
)

/** `PUT /api/v1/notes/{id}` replaces every editable field. A `null` [folderId] makes the note unfiled. */
@Serializable
data class NetworkUpdateNoteRequest(
    val title: String,
    val content: String,
    val folderId: String?,
)

@Serializable
data class NetworkNoteFolder(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
)

/** `POST /api/v1/notes/folders` and `PUT /api/v1/notes/folders/{id}` */
@Serializable
data class NetworkNoteFolderRequest(
    val name: String,
)
