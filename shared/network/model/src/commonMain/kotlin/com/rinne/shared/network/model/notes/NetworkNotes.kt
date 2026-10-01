package com.rinne.shared.network.model.notes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NetworkNoteSource {
    USER,
    AI_ASSISTANT,
}

/**
 * A full note, including its rich-text body.
 *
 * [text] and [dateTime] are the legacy plain-text fields. They are retained for one compatibility
 * release so older clients keep working: the backend derives [text] from [content] and never lets a
 * legacy write erase an existing rich-text document.
 */
@Serializable
data class NetworkNoteInfo(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("folderId") val folderId: String? = null,
    @SerialName("content") val content: NetworkRichTextDocument = NetworkRichTextDocument.Empty,
    @SerialName("plainTextPreview") val plainTextPreview: String = "",
    @SerialName("source") val source: NetworkNoteSource = NetworkNoteSource.USER,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("revision") val revision: Long = 0,

    @Deprecated("Legacy plain-text body; use content. Retained for the compatibility release.")
    @SerialName("text") val text: String = "",
    @Deprecated("Legacy timestamp; use updatedAt. Retained for the compatibility release.")
    @SerialName("dateTime") val dateTime: String = updatedAt,
)

/** A note as it appears in a list response: everything but the rich-text body. */
@Serializable
data class NetworkNoteSummary(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("folderId") val folderId: String? = null,
    @SerialName("plainTextPreview") val plainTextPreview: String = "",
    @SerialName("source") val source: NetworkNoteSource = NetworkNoteSource.USER,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("revision") val revision: Long = 0,
)

/**
 * One page of note summaries, ordered by `updatedAt` descending.
 *
 * A null [nextCursor] means the last page; otherwise it is passed back as the `cursor` query
 * parameter to fetch the following page.
 */
@Serializable
data class NetworkNotesPage(
    @SerialName("items") val items: List<NetworkNoteSummary> = emptyList(),
    @SerialName("nextCursor") val nextCursor: String? = null,
)

@Serializable
data class NetworkCreateNoteBody(
    @SerialName("title") val title: String = "",
    @SerialName("folderId") val folderId: String? = null,
    @SerialName("content") val content: NetworkRichTextDocument = NetworkRichTextDocument.Empty,
    @SerialName("source") val source: NetworkNoteSource = NetworkNoteSource.USER,
)

/**
 * Partial note update carrying the full current document.
 *
 * [clientMutationId] makes the write idempotent: replaying the same id for the same note returns
 * the originally accepted result instead of applying the mutation twice. As on
 * [NetworkUpdateNoteFolderBody], [updateFolder] disambiguates a null [folderId] ("move to
 * `Unfiled`") from an absent one ("leave the placement alone").
 */
@Serializable
data class NetworkUpdateNoteBody(
    @SerialName("clientMutationId") val clientMutationId: String,
    @SerialName("title") val title: String? = null,
    @SerialName("content") val content: NetworkRichTextDocument? = null,
    @SerialName("folderId") val folderId: String? = null,
    @SerialName("updateFolder") val updateFolder: Boolean = false,
)

/** Moves [noteIds] into [folderId], or to `Unfiled` when it is null. Applied atomically. */
@Serializable
data class NetworkMoveNotesBody(
    @SerialName("noteIds") val noteIds: List<String>,
    @SerialName("folderId") val folderId: String? = null,
)

@Serializable
data class NetworkMoveNotesResult(
    @SerialName("notes") val notes: List<NetworkNoteSummary> = emptyList(),
)

/** The `422` payload returned when a note or folder write breaks the document contract. */
@Serializable
data class NetworkNoteValidationError(
    @SerialName("violations") val violations: List<NetworkNoteViolation> = emptyList(),
)
