package com.rinne.shared.network.model.notes

import com.rinne.libraries.date.time.core.RinneTimestamp
import kotlinx.serialization.Serializable

/**
 * One version of a note's history (`GET /api/v1/notes/{id}/versions`): an editing session, kept on the
 * server only (not synced). The newest version is the [current] one.
 */
@Serializable
data class NetworkNoteVersion(
    val id: String,
    val noteId: String,
    val authorId: String,
    /** Who made it when not the user's own device, e.g. an MCP connection's name; `null` for sync. */
    val authorLabel: String? = null,
    val title: String,
    val startedAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val name: String? = null,
    val current: Boolean,
    /** The version this one restored (its summary's kind is then `restored`). */
    val restoredFrom: String? = null,
    val summary: NetworkNoteVersionSummary,
)

/** What a version changed against the one before it; the client words it from the string tables. */
@Serializable
data class NetworkNoteVersionSummary(
    /** [NetworkNoteVersionKind]. */
    val kind: String,
    val sentencesAdded: Int = 0,
    val sentencesRemoved: Int = 0,
    val wordsChanged: Int = 0,
    val checklistAdded: Int = 0,
    val checklistChecked: Int = 0,
    val sectionsAdded: List<String> = emptyList(),
    val sectionsRemoved: List<String> = emptyList(),
    val restoredFrom: String? = null,
)

object NetworkNoteVersionKind {
    const val Edit = "edit"
    const val Created = "created"
    const val Restored = "restored"
}

/** A version with its text (`GET /api/v1/notes/{id}/versions/{versionId}`). */
@Serializable
data class NetworkNoteVersionContent(
    val version: NetworkNoteVersion,
    val content: String,
)

/** A page of versions, newest first; [next] is the `before` of the next page, `null` on the last. */
@Serializable
data class NetworkNoteVersionsPage(
    val versions: List<NetworkNoteVersion>,
    val next: String? = null,
)

/** `PUT /api/v1/notes/{id}/versions/{versionId}/name`: a blank or `null` name clears it; at most 80 characters. */
@Serializable
data class NetworkNameNoteVersionRequest(val name: String? = null)

/** `POST /api/v1/notes/{id}/versions/seal`: seals the newest version; the next one records [restoredFrom]. */
@Serializable
data class NetworkSealNoteVersionsRequest(val restoredFrom: String? = null)

object NoteHistoryErrorCodes {
    const val NoteNotFound = "notes.note_not_found"
    const val VersionNotFound = "notes.version_not_found"
}
