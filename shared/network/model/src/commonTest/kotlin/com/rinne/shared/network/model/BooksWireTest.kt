package com.rinne.shared.network.model

import com.rinne.shared.network.model.attachments.NetworkAttachment
import com.rinne.shared.network.model.books.NetworkBook
import com.rinne.shared.network.model.books.NetworkBookAnnotation
import com.rinne.shared.network.model.books.NetworkBookFolder
import com.rinne.shared.network.model.books.NetworkBookHighlightStatus
import com.rinne.shared.network.model.books.NetworkBooks
import com.rinne.shared.network.model.sync.SyncFieldValue
import com.rinne.shared.network.model.sync.readSyncField
import com.rinne.shared.network.model.sync.syncFields
import com.rinne.shared.network.model.tasks.NetworkTaskStatus
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BooksWireTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test fun `a minimal book decodes with the defaults`() {
        val wire = """{"id":"b","title":"Wissenschaft der Logik","createdAt":"2026-11-03T00:00:00Z","updatedAt":"2026-11-03T00:00:00Z",""" +
            """"format":"epub","fileSha256":"${"a".repeat(64)}","fileSize":1024,"fileName":"logik.epub"}"""
        val book = json.decodeFromString(NetworkBook.serializer(), wire)
        assertEquals(NetworkBook.Status.None, book.status)
        assertEquals(0.0, book.progress)
        assertEquals(emptyList(), book.authors)
        assertEquals(book, json.decodeFromString(NetworkBook.serializer(), json.encodeToString(NetworkBook.serializer(), book)))
    }

    @Test fun `a position round-trips as fields`() {
        val fields = syncFields {
            set(NetworkBook.Fields.Position, "epubcfi(/6/4!/4/2:10)")
            set(NetworkBook.Fields.Progress, 0.38)
            set(NetworkBook.Fields.PositionLabel, null)
        }
        assertEquals(SyncFieldValue.Present("epubcfi(/6/4!/4/2:10)"), fields.readSyncField(NetworkBook.Fields.Position))
        assertEquals(SyncFieldValue.Present(0.38), fields.readSyncField(NetworkBook.Fields.Progress))
        assertEquals(SyncFieldValue.Present(null), fields.readSyncField(NetworkBook.Fields.PositionLabel))
    }

    @Test fun `create-only properties are sent with the create - ids and timestamps never`() {
        val bookFields = NetworkBook.Fields.All.map { it.name }
        listOf("id", "createdAt", "updatedAt").forEach { assertFalse(it in bookFields, it) }
        listOf("format", "fileSha256", "fileSize").forEach { assertTrue(it in bookFields, it) }
        val annotationFields = NetworkBookAnnotation.Fields.All.map { it.name }
        listOf("id", "createdAt", "updatedAt").forEach { assertFalse(it in annotationFields, it) }
        listOf("bookId", "kind").forEach { assertTrue(it in annotationFields, it) }
    }

    @Test fun `the fixed sets name every value the wire allows`() {
        assertEquals(listOf("none", "want", "reading", "finished"), NetworkBook.Status.All)
        assertEquals(listOf("epub", "pdf", "fb2", "mobi", "azw3", "cbz", "txt", "html", "md"), NetworkBook.Formats.All)
        assertEquals(listOf("yellow", "green", "blue", "pink"), NetworkBookAnnotation.Colors.Highlights)
        assertEquals(listOf("yellow", "green", "blue", "pink", "orange"), NetworkBookAnnotation.Colors.All)
        assertEquals(listOf("new", "reread", "to_use", "used", "archived"), NetworkBookHighlightStatus.BuiltIn.All)
        assertEquals(listOf("highlight", "underline", "squiggly"), NetworkBookAnnotation.Styles.All)
        assertEquals(listOf("highlight", "bookmark"), NetworkBookAnnotation.Kinds.All)
        assertEquals(200L * 1024 * 1024, NetworkBooks.MaxFileSize)
        assertTrue(NetworkBooks.MaxCoverSize < NetworkBooks.MaxFileSize)
        assertEquals("book", NetworkAttachment.OwnerBook)
        assertEquals("book_cover", NetworkAttachment.OwnerBookCover)
    }

    @Test fun `book references name a book - a highlight and the highlights list`() {
        assertEquals("rinne://book/b1", NetworkBooks.reference("b1"))
        assertEquals("rinne://book/b1#a1", NetworkBooks.annotationReference("b1", "a1"))
        assertEquals("rinne://book/b1#highlights", NetworkBooks.highlightsReference("b1"))
        assertEquals(NetworkBooks.ReferencePart("b1", null), NetworkBooks.parseReference("rinne://book/b1"))
        assertEquals(NetworkBooks.ReferencePart("b1", "a1"), NetworkBooks.parseReference("rinne://book/b1#a1"))
        assertEquals(null, NetworkBooks.parseReference("rinne://note/b1"))
        assertEquals(null, NetworkBooks.parseReference("rinne://book/"))
    }

    @Test fun `a folder and an annotation decode with the defaults`() {
        val folder = json.decodeFromString(
            NetworkBookFolder.serializer(),
            """{"id":"f","name":"Philosophie","createdAt":"2026-11-03T00:00:00Z","updatedAt":"2026-11-03T00:00:00Z"}""",
        )
        assertFalse(folder.favorite)
        val annotation = json.decodeFromString(
            NetworkBookAnnotation.serializer(),
            """{"id":"a","bookId":"b","kind":"bookmark","createdAt":"2026-11-03T00:00:00Z","updatedAt":"2026-11-03T00:00:00Z","start":"page:3"}""",
        )
        assertEquals(NetworkBookAnnotation.Colors.Yellow, annotation.color)
        assertEquals(null, annotation.end)
        // an older app sends none of the print reader's fields: they keep their defaults
        assertEquals(null, annotation.status)
        assertFalse(annotation.pinned)
        assertEquals(null, annotation.title)
    }

    @Test fun `a highlight status decodes with the defaults and round-trips its fields`() {
        val status = json.decodeFromString(
            NetworkBookHighlightStatus.serializer(),
            """{"id":"s","name":"Essay","createdAt":"2026-11-24T00:00:00Z","updatedAt":"2026-11-24T00:00:00Z"}""",
        )
        assertEquals(NetworkTaskStatus.Colors.Primary, status.color)
        assertEquals(null, status.position)
        assertEquals("book_highlight_status", NetworkBookHighlightStatus.SyncType)
        val fields = syncFields {
            set(NetworkBookHighlightStatus.Fields.Name, "Essay")
            set(NetworkBookHighlightStatus.Fields.Position, null)
            set(NetworkBookAnnotation.Fields.Status, NetworkBookHighlightStatus.BuiltIn.Reread)
            set(NetworkBookAnnotation.Fields.Pinned, true)
        }
        assertEquals(SyncFieldValue.Present("Essay"), fields.readSyncField(NetworkBookHighlightStatus.Fields.Name))
        assertEquals(SyncFieldValue.Present(null), fields.readSyncField(NetworkBookHighlightStatus.Fields.Position))
        assertEquals(SyncFieldValue.Present("reread"), fields.readSyncField(NetworkBookAnnotation.Fields.Status))
        assertEquals(SyncFieldValue.Present(true), fields.readSyncField(NetworkBookAnnotation.Fields.Pinned))
    }
}
