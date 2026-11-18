package com.rinne.shared.network.model.comments

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/**
 * A comment thread on a range of a note's text, as synced. The range itself lives in the note's text (hidden
 * marks `<!--c:<id>-->…<!--/c:<id>-->`), so it follows the note's edits. Writable fields: `noteId` (the note
 * it is on), `status` ([StatusOpen] or [StatusArchived]), `archivedReason` (`null`, or [ReasonTextDeleted]
 * when the apps archived it because its marks left the text), `quote` (the commented text when the thread
 * began). The apps delete a thread together with its comments, and a note's threads with the note; the
 * server keeps no references between them.
 */
@Serializable
data class NetworkNoteCommentThread(
    val id: String,
    val noteId: String,
    val status: String,
    val quote: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val archivedReason: String? = null,
) {
    companion object {
        const val SyncType = "note_comment_thread"

        const val StatusOpen = "open"
        const val StatusArchived = "archived"
        val Statuses: Set<String> = setOf(StatusOpen, StatusArchived)

        /** Its text left the note: archived by the apps, opened again when the text comes back. */
        const val ReasonTextDeleted = "text_deleted"
        val Reasons: Set<String> = setOf(ReasonTextDeleted)
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val NoteId = SyncField("noteId", String.serializer())
        val Status = SyncField("status", String.serializer())
        val ArchivedReason = SyncField("archivedReason", String.serializer().nullable)
        val Quote = SyncField("quote", String.serializer())

        val All: List<SyncField<*>> = listOf(NoteId, Status, ArchivedReason, Quote)
    }
}

/**
 * A comment of a thread, as synced. Writable fields: `threadId`, `text` (Markdown: bold and italic),
 * `editedAt` (when its text last changed after it was written; `null`: never). `authorId` is the user who
 * wrote it: set by the server from the push's account, never written by the apps.
 */
@Serializable
data class NetworkNoteComment(
    val id: String,
    val threadId: String,
    val authorId: String,
    val text: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val editedAt: RinneTimestamp? = null,
) {
    companion object {
        const val SyncType = "note_comment"

        const val TextMaxLength = 10_000
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val ThreadId = SyncField("threadId", String.serializer())
        val Text = SyncField("text", String.serializer())
        val EditedAt = SyncField("editedAt", RinneTimestamp.serializer().nullable)

        val All: List<SyncField<*>> = listOf(ThreadId, Text, EditedAt)
    }
}
