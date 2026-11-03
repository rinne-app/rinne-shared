package com.rinne.shared.network.model.books

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/** What every Books entity shares: limits and the `rinne://book` references. */
object NetworkBooks {
    /** The largest book file the server accepts (an attachment of owner type `book`). */
    const val MaxFileSize: Long = 200L * 1024 * 1024

    /** The largest cover image (an attachment of owner type `book_cover`: JPEG or PNG). */
    const val MaxCoverSize: Long = 2L * 1024 * 1024

    /** The scheme and kind of a book reference: `rinne://book/<id>`. */
    const val ReferencePrefix = "rinne://book/"

    /** The fragment of the live list of a book's highlights: `rinne://book/<id>#highlights`. */
    const val HighlightsFragment = "highlights"

    fun reference(bookId: String): String = ReferencePrefix + bookId

    fun annotationReference(bookId: String, annotationId: String): String = "$ReferencePrefix$bookId#$annotationId"

    fun highlightsReference(bookId: String): String = "$ReferencePrefix$bookId#$HighlightsFragment"

    /** A book reference split into the book and its fragment (an annotation's id or [HighlightsFragment]). */
    data class ReferencePart(val bookId: String, val fragment: String?)

    /** The parts of [reference], or null when it isn't a book reference. */
    fun parseReference(reference: String): ReferencePart? {
        if (!reference.startsWith(ReferencePrefix)) return null
        val rest = reference.removePrefix(ReferencePrefix)
        val bookId = rest.substringBefore('#')
        val fragment = rest.substringAfter('#', missingDelimiterValue = "").takeIf { it.isNotEmpty() }
        return ReferencePart(bookId, fragment).takeIf { bookId.isNotEmpty() && '/' !in bookId }
    }
}

/**
 * A book in the library, as synced. Writable fields: everything but `id` and `createdAt`; `format`,
 * `fileSha256` and `fileSize` describe the file and are create-only (sent with the create, rejected
 * when an update changes them). The file itself is an attachment of owner type
 * `book`, its cover one of `book_cover`. `position` is a locator of the reading engine (an EPUB CFI or
 * `page:<n>`; never a page number of a layout), `progress` the share of the book read (0…1),
 * `positionLabel` the chapter there, `positionAt` when the position was written (last write wins).
 * `startedOn`/`finishedOn` are ISO dates. Deleting a book deletes its annotations and attachments.
 */
@Serializable
data class NetworkBook(
    val id: String,
    val title: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val format: String,
    val fileSha256: String,
    val fileSize: Long,
    val fileName: String,
    val authors: List<String> = emptyList(),
    val language: String? = null,
    val pages: Int? = null,
    val chapters: Int? = null,
    val description: String = "",
    val publisher: String? = null,
    val published: String? = null,
    val folderId: String? = null,
    val status: String = Status.None,
    val startedOn: String? = null,
    val finishedOn: String? = null,
    val pinned: Boolean = false,
    val onHome: Boolean = false,
    val noteId: String? = null,
    val position: String? = null,
    val progress: Double = 0.0,
    val positionLabel: String? = null,
    val positionAt: RinneTimestamp? = null,
) {
    companion object {
        const val SyncType = "book"
    }

    /** The writable fields: the keys of push `fields` ([Format], [FileSha256] and [FileSize] create-only). */
    object Fields {
        val Format = SyncField("format", String.serializer())
        val FileSha256 = SyncField("fileSha256", String.serializer())
        val FileSize = SyncField("fileSize", Long.serializer())
        val Title = SyncField("title", String.serializer())
        val FileName = SyncField("fileName", String.serializer())
        val Authors = SyncField("authors", ListSerializer(String.serializer()))
        val Language = SyncField("language", String.serializer().nullable)
        val Pages = SyncField("pages", Int.serializer().nullable)
        val Chapters = SyncField("chapters", Int.serializer().nullable)
        val Description = SyncField("description", String.serializer())
        val Publisher = SyncField("publisher", String.serializer().nullable)
        val Published = SyncField("published", String.serializer().nullable)
        val FolderId = SyncField("folderId", String.serializer().nullable)
        val Status = SyncField("status", String.serializer())
        val StartedOn = SyncField("startedOn", String.serializer().nullable)
        val FinishedOn = SyncField("finishedOn", String.serializer().nullable)
        val Pinned = SyncField("pinned", Boolean.serializer())
        val OnHome = SyncField("onHome", Boolean.serializer())
        val NoteId = SyncField("noteId", String.serializer().nullable)
        val Position = SyncField("position", String.serializer().nullable)
        val Progress = SyncField("progress", Double.serializer())
        val PositionLabel = SyncField("positionLabel", String.serializer().nullable)
        val PositionAt = SyncField("positionAt", RinneTimestamp.serializer().nullable)

        val All: List<SyncField<*>> = listOf(
            Format, FileSha256, FileSize, Title, FileName, Authors, Language, Pages, Chapters, Description, Publisher, Published, FolderId, Status,
            StartedOn, FinishedOn, Pinned, OnHome, NoteId, Position, Progress, PositionLabel, PositionAt,
        )
    }

    /** The keys of a book's reading status. */
    object Status {
        const val None = "none"
        const val WantToRead = "want"
        const val Reading = "reading"
        const val Finished = "finished"

        val All: List<String> = listOf(None, WantToRead, Reading, Finished)
    }

    /** The keys of the file formats (the reader's formats). */
    object Formats {
        const val Epub = "epub"
        const val Pdf = "pdf"
        const val Fb2 = "fb2"
        const val Mobi = "mobi"
        const val Azw3 = "azw3"
        const val Cbz = "cbz"
        const val Txt = "txt"
        const val Html = "html"
        const val Markdown = "md"

        val All: List<String> = listOf(Epub, Pdf, Fb2, Mobi, Azw3, Cbz, Txt, Html, Markdown)
    }
}

/**
 * A folder of books, as synced. Writable fields: `name`, `parentId` (the folder it is in; `null` = top
 * level), `favorite` (listed first), `pinned` (in the library's Pinned), `position` (its place in the
 * user's order: a key compared as text). Deleting a folder moves its books and subfolders one level up.
 */
@Serializable
data class NetworkBookFolder(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val parentId: String? = null,
    val favorite: Boolean = false,
    val pinned: Boolean = false,
    val position: String? = null,
) {
    companion object {
        const val SyncType = "book_folder"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val ParentId = SyncField("parentId", String.serializer().nullable)
        val Favorite = SyncField("favorite", Boolean.serializer())
        val Pinned = SyncField("pinned", Boolean.serializer())
        val Position = SyncField("position", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Name, ParentId, Favorite, Pinned, Position)
    }
}

/**
 * A highlight or a bookmark in a book, as synced. `bookId` and `kind` are create-only; writable fields:
 * `start`/`end` (engine locators; a bookmark has no `end`), `text` (the quote, a bookmark's excerpt),
 * `note`, `color`, `style`, `chapter` (its chapter's title when made), `progress` (where it is in the
 * book, 0…1, for ordering without a layout).
 */
@Serializable
data class NetworkBookAnnotation(
    val id: String,
    val bookId: String,
    val kind: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val start: String,
    val end: String? = null,
    val text: String = "",
    val note: String = "",
    val color: String = Colors.Yellow,
    val style: String = Styles.Highlight,
    val chapter: String? = null,
    val progress: Double = 0.0,
) {
    companion object {
        const val SyncType = "book_annotation"
    }

    /** The writable fields: the keys of push `fields` ([BookId] and [Kind] create-only). */
    object Fields {
        val BookId = SyncField("bookId", String.serializer())
        val Kind = SyncField("kind", String.serializer())
        val Start = SyncField("start", String.serializer())
        val End = SyncField("end", String.serializer().nullable)
        val Text = SyncField("text", String.serializer())
        val Note = SyncField("note", String.serializer())
        val Color = SyncField("color", String.serializer())
        val Style = SyncField("style", String.serializer())
        val Chapter = SyncField("chapter", String.serializer().nullable)
        val Progress = SyncField("progress", Double.serializer())

        val All: List<SyncField<*>> = listOf(BookId, Kind, Start, End, Text, Note, Color, Style, Chapter, Progress)
    }

    object Kinds {
        const val Highlight = "highlight"
        const val Bookmark = "bookmark"

        val All: List<String> = listOf(Highlight, Bookmark)
    }

    object Colors {
        const val Yellow = "yellow"
        const val Green = "green"
        const val Blue = "blue"
        const val Pink = "pink"

        val All: List<String> = listOf(Yellow, Green, Blue, Pink)
    }

    object Styles {
        const val Highlight = "highlight"
        const val Underline = "underline"
        const val Squiggly = "squiggly"

        val All: List<String> = listOf(Highlight, Underline, Squiggly)
    }
}
