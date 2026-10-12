package com.rinne.shared.network.model.attachments

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/**
 * A file attached to an entity (a note's voice note), as synced: its metadata only — the bytes go to
 * object storage through the URLs of [NetworkAttachmentTransfer] (offline sync design §8). Writable
 * fields: the owner (`ownerType`, `ownerId`), `fileName`, `mimeType`, `size` and `sha256` (fixed once
 * the file is uploaded), and the audio details `durationMillis`, `waveform` (the levels as hex bytes,
 * two characters per bar), `transcript`, `transcriptLanguage`. [uploaded] is the server's: set when the
 * upload is confirmed.
 */
@Serializable
data class NetworkAttachment(
    val id: String,
    val ownerType: String,
    val ownerId: String,
    val fileName: String,
    val mimeType: String,
    val size: Long,
    val sha256: String,
    val uploaded: Boolean,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val durationMillis: Long? = null,
    val waveform: String? = null,
    val transcript: String? = null,
    val transcriptLanguage: String? = null,
) {
    companion object {
        const val SyncType = "attachment"

        /** [ownerType] of a note's attachment. */
        const val OwnerNote = "note"

        /** The largest file the server accepts (offline sync design §8). */
        const val MaxSize = 1L shl 30
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val OwnerType = SyncField("ownerType", String.serializer())
        val OwnerId = SyncField("ownerId", String.serializer())
        val FileName = SyncField("fileName", String.serializer())
        val MimeType = SyncField("mimeType", String.serializer())
        val Size = SyncField("size", Long.serializer())
        val Sha256 = SyncField("sha256", String.serializer())
        val DurationMillis = SyncField("durationMillis", Long.serializer().nullable)
        val Waveform = SyncField("waveform", String.serializer().nullable)
        val Transcript = SyncField("transcript", String.serializer().nullable)
        val TranscriptLanguage = SyncField("transcriptLanguage", String.serializer().nullable)

        val All: List<SyncField<*>> =
            listOf(OwnerType, OwnerId, FileName, MimeType, Size, Sha256, DurationMillis, Waveform, Transcript, TranscriptLanguage)
    }
}

/**
 * `POST /api/v1/sync/attachments/{id}/upload-url` and `GET …/download-url`: a short-lived presigned
 * URL of the attachment's object. Send [method] to [url] with exactly [headers] (an upload: the file's
 * bytes as the body).
 */
@Serializable
data class NetworkAttachmentTransfer(
    val url: String,
    val method: String,
    val headers: Map<String, String>,
    val expiresAt: RinneTimestamp,
)

/** `POST /api/v1/sync/attachments/{id}/confirm`: the stored object matched; the entity's new [version]. */
@Serializable
data class NetworkAttachmentConfirmation(
    val uploaded: Boolean,
    val version: Long,
)

/** Error codes of the attachment endpoints. */
object AttachmentErrorCodes {
    const val NotFound = "attachments.not_found"

    /** Confirmed before the object was stored, or the stored object differs from the declared size. */
    const val NotUploaded = "attachments.not_uploaded"

    /** Already uploaded: its file can't be replaced. */
    const val AlreadyUploaded = "attachments.already_uploaded"

    /** The declared size is above [NetworkAttachment.MaxSize]. */
    const val FileTooLarge = "sync.file_too_large"

    /** Object storage isn't configured or doesn't answer: try again later. */
    const val StorageUnavailable = "attachments.storage_unavailable"
}
