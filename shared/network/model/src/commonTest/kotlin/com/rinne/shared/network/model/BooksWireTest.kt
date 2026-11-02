package com.rinne.shared.network.model

import com.rinne.shared.network.model.attachments.NetworkAttachment
import com.rinne.shared.network.model.books.NetworkBook
import com.rinne.shared.network.model.books.NetworkBookAnnotation
import com.rinne.shared.network.model.books.NetworkBookFolder
import com.rinne.shared.network.model.books.NetworkBooks
import com.rinne.shared.network.model.sync.SyncFieldValue
import com.rinne.shared.network.model.sync.readSyncField
import com.rinne.shared.network.model.sync.syncFields
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

    @Test fun `create-only properties are not writable fields`() {
        val bookFields = NetworkBook.Fields.All.map { it.name }
        listOf("id", "createdAt", "updatedAt", "format", "fileSha256", "fileSize").forEach { assertFalse(it in bookFields, it) }
        val annotationFields = NetworkBookAnnotation.Fields.All.map { it.name }
        listOf("bookId", "kind").forEach { assertFalse(it in annotationFields, it) }
    }

    @Test fun `the fixed sets name every value the wire allows`() {
        assertEquals(listOf("none", "want", "reading", "finished"), NetworkBook.Status.All)
        assertEquals(listOf("epub", "pdf", "fb2", "mobi", "azw3", "cbz", "txt", "html", "md"), NetworkBook.Formats.All)
        assertEquals(listOf("yellow", "green", "blue", "pink"), NetworkBookAnnotation.Colors.All)
        assertEquals(listOf("highlight", "underline", "squiggly"), NetworkBookAnnotation.Styles.All)
        assertEquals(listOf("highlight", "bookmark"), NetworkBookAnnotation.Kinds.All)
        assertEquals(200L * 1024 * 1024, NetworkBooks.MaxFileSize)
        assertTrue(NetworkBooks.MaxCoverSize < NetworkBooks.MaxFileSize)
        assertEquals("book", NetworkAttachment.OwnerBook)
        assertEquals("book_cover", NetworkAttachment.OwnerBookCover)
    }

    @Test fun `book references name a book, a highlight and the highlights list`() {
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
    }
}
