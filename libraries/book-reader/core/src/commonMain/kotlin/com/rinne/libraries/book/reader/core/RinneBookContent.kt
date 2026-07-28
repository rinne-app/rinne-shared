package com.rinne.libraries.book.reader.core

data class RinneBook(
    val content: List<RinneBookContent>,
    val footnotes: Map<String, String> = emptyMap(),
    val images: Map<String, String> = emptyMap(),
    val imageBinaries: Map<String, RinneBookBinaryImage> = emptyMap(),
    val imageCaptions: Map<String, String> = emptyMap(),
)

data class RinneBookBinaryImage(
    val contentType: String,
    val base64: String,
)

sealed interface RinneBookContent {
    data class Title(val text: String) : RinneBookContent
    data class Subtitle(val text: String) : RinneBookContent
    data class Epigraph(val text: String) : RinneBookContent
    data class Cite(val text: String) : RinneBookContent
    data class PoemLine(val text: String) : RinneBookContent
    data class TextAuthor(val text: String) : RinneBookContent
    data class Date(val text: String) : RinneBookContent
    data class NoteReference(
        val text: String,
        val noteId: String,
    ) : RinneBookContent
    data class Image(
        val refId: String?,
        val description: String? = null,
    ) : RinneBookContent
    data class Table(
        val rows: List<List<String>>,
    ) : RinneBookContent
    data class Text(
        val text: String,
        val bold: Boolean = false,
        val italic: Boolean = false,
    ) : RinneBookContent
    data class LineBreak(val count: Int = 1) : RinneBookContent
}