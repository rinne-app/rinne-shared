package com.rinne.libraries.book.reader.fb2

import com.rinne.libraries.book.reader.core.RinneBook
import com.rinne.libraries.book.reader.core.RinneBookBinaryImage
import com.rinne.libraries.book.reader.core.RinneBookContent
import com.rinne.libraries.serialization.xml.RinneXmlSerialization
import com.rinne.libraries.serialization.xml.XmlTag
import com.rinne.libraries.serialization.xml.XmlTagContent

class RinneBookReaderFb2 {

    fun read(raw: String): RinneBook {
        val result = RinneXmlSerialization().decode(raw)
        val footnotes = result.extractFootnotes()
        val images = result.extractImages()
        val imageBinaries = result.extractImageBinaries()
        val imageCaptions = result.extractImageCaptions()

        val content = buildList<RinneBookContent> {
            val bodies = result.content
                .filterIsInstance<XmlTagContent.Tag>()
                .map { it.tag }
                .filter { it.tag.equals("body", ignoreCase = true) }
                .filterNot { body -> body.getAttribute("name").equals("notes", ignoreCase = true) }

            if (bodies.isEmpty()) {
                parseBlockTag(result, knownFootnotes = footnotes)
            } else {
                bodies.forEach { body ->
                    parseBlockTag(body, knownFootnotes = footnotes)
                }
            }
        }

        return RinneBook(
            content = content,
            footnotes = footnotes,
            images = images,
            imageBinaries = imageBinaries,
            imageCaptions = imageCaptions,
        )
    }

    private fun MutableList<RinneBookContent>.parseBlockTag(
        tag: XmlTag,
        inheritedStyle: InlineStyle = InlineStyle(),
        knownFootnotes: Map<String, String> = emptyMap(),
    ) {
        when (tag.tag.lowercase()) {
            "title" -> appendParagraph(tag, BlockRole.Title, inheritedStyle, knownFootnotes)
            "subtitle" -> appendParagraph(tag, BlockRole.Subtitle, inheritedStyle, knownFootnotes)
            "p" -> appendParagraph(tag, BlockRole.Body, inheritedStyle, knownFootnotes)
            "v" -> appendParagraph(tag, BlockRole.Poem, inheritedStyle, knownFootnotes)
            "image" -> appendImage(tag)
            "table" -> appendTable(tag)
            "text-author" -> appendParagraph(tag, BlockRole.TextAuthor, inheritedStyle, knownFootnotes)
            "date" -> appendParagraph(tag, BlockRole.Date, inheritedStyle, knownFootnotes)
            "epigraph" -> parseContainer(tag, BlockRole.Epigraph, inheritedStyle.copy(italic = true), knownFootnotes)
            "cite" -> parseContainer(tag, BlockRole.Cite, inheritedStyle.copy(italic = true), knownFootnotes)
            "poem" -> parseContainer(tag, BlockRole.Poem, inheritedStyle, knownFootnotes)
            "empty-line" -> add(RinneBookContent.LineBreak())
            "strong", "b" -> parseInline(tag.content, inheritedStyle.copy(bold = true), BlockRole.Body, knownFootnotes)
            "emphasis", "i" -> parseInline(tag.content, inheritedStyle.copy(italic = true), BlockRole.Body, knownFootnotes)
            else -> tag.content.forEach { child ->
                when (child) {
                    is XmlTagContent.Text -> {
                        val text = child.value.trim()
                        if (text.isNotEmpty()) {
                            add(
                                RinneBookContent.Text(
                                    text = text,
                                    bold = inheritedStyle.bold,
                                    italic = inheritedStyle.italic,
                                )
                            )
                            add(RinneBookContent.LineBreak())
                        }
                    }

                    is XmlTagContent.Tag -> parseBlockTag(child.tag, inheritedStyle, knownFootnotes)
                }
            }
        }
    }

    private fun MutableList<RinneBookContent>.parseContainer(
        tag: XmlTag,
        role: BlockRole,
        inheritedStyle: InlineStyle,
        knownFootnotes: Map<String, String>,
    ) {
        tag.content.forEach { child ->
            when (child) {
                is XmlTagContent.Text -> {
                    val text = child.value.normalizeFb2Text()
                    if (text.isNotEmpty()) {
                        when (role) {
                            BlockRole.Epigraph -> add(RinneBookContent.Epigraph(text))
                            BlockRole.Cite -> add(RinneBookContent.Cite(text))
                            BlockRole.Poem -> add(RinneBookContent.PoemLine(text))
                            else -> add(
                                RinneBookContent.Text(
                                    text = text,
                                    bold = inheritedStyle.bold,
                                    italic = inheritedStyle.italic,
                                )
                            )
                        }
                        add(RinneBookContent.LineBreak())
                    }
                }

                is XmlTagContent.Tag -> {
                    when (child.tag.tag.lowercase()) {
                        "p", "v", "text-author", "date", "subtitle", "title" ->
                            appendParagraph(child.tag, role, inheritedStyle, knownFootnotes)

                        else -> parseBlockTag(child.tag, inheritedStyle, knownFootnotes)
                    }
                }
            }
        }
        add(RinneBookContent.LineBreak())
    }

    private fun MutableList<RinneBookContent>.appendParagraph(
        tag: XmlTag,
        role: BlockRole,
        inheritedStyle: InlineStyle,
        knownFootnotes: Map<String, String>,
    ) {
        val chunks = collectInlineText(tag.content, inheritedStyle)
        val paragraphImages = tag.collectImageRefs()
        if (chunks.isEmpty() && paragraphImages.isEmpty()) return
        if (chunks.isEmpty() && paragraphImages.isNotEmpty()) {
            paragraphImages.forEach { imageRef ->
                add(RinneBookContent.Image(refId = imageRef))
                add(RinneBookContent.LineBreak(2))
            }
            return
        }

        when (role) {
            BlockRole.Title -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Title(text))
            }

            BlockRole.Subtitle -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Subtitle(text))
            }

            BlockRole.Body -> {
                val normalizedChunks = chunks.mapNotNull { chunk ->
                    val normalized = chunk.text.normalizeFb2Text()
                    if (normalized.isEmpty()) null else chunk.copy(text = normalized)
                }

                if (normalizedChunks.isEmpty()) return
                val fullParagraphText = normalizedChunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (fullParagraphText.isFootnoteHeading()) return

                val footnoteDefinitionId = extractFootnoteDefinitionId(fullParagraphText)
                if (footnoteDefinitionId != null && knownFootnotes.containsKey(footnoteDefinitionId)) return

                // In many FB2 books headings are encoded as <p><strong>...</strong></p>.
                val singleStrongParagraph = normalizedChunks.size == 1 &&
                    normalizedChunks.first().style.bold &&
                    !normalizedChunks.first().style.italic

                if (singleStrongParagraph) {
                    add(RinneBookContent.Subtitle(normalizedChunks.first().text))
                } else {
                    add(RinneBookContent.Text(text = "    "))
                    normalizedChunks.forEachIndexed { index, chunk ->
                        if (chunk.style.noteId != null && chunk.text.isNotBlank()) {
                            add(RinneBookContent.NoteReference(text = chunk.text, noteId = chunk.style.noteId))
                        } else {
                            addBodyChunkWithImplicitFootnotes(
                                text = chunk.text,
                                bold = chunk.style.bold,
                                italic = chunk.style.italic,
                                knownFootnotes = knownFootnotes,
                            )
                        }

                        val next = normalizedChunks.getOrNull(index + 1)
                        if (next != null && needsSpaceBetween(chunk.text, next.text)) {
                            add(RinneBookContent.Text(text = " "))
                        }
                    }
                }
            }

            BlockRole.Epigraph -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Epigraph(text))
            }

            BlockRole.Cite -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Cite(text))
            }

            BlockRole.Poem -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.PoemLine(text))
            }

            BlockRole.TextAuthor -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.TextAuthor(text))
            }

            BlockRole.Date -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Date(text))
            }
        }

        val lineBreakCount = when (role) {
            BlockRole.Poem -> 1
            else -> 2
        }
        add(RinneBookContent.LineBreak(lineBreakCount))
        paragraphImages.forEach { imageRef ->
            add(RinneBookContent.Image(refId = imageRef))
            add(RinneBookContent.LineBreak(2))
        }
    }

    private fun MutableList<RinneBookContent>.parseInline(
        content: List<XmlTagContent>,
        inheritedStyle: InlineStyle,
        role: BlockRole,
        knownFootnotes: Map<String, String>,
    ) {
        val chunks = collectInlineText(content, inheritedStyle)
        if (chunks.isEmpty()) return

        when (role) {
            BlockRole.Title -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Title(text))
            }

            BlockRole.Subtitle -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Subtitle(text))
            }

            BlockRole.Body -> {
                add(RinneBookContent.Text(text = "    "))
                chunks.forEach {
                    val normalized = it.text.normalizeFb2Text()
                    if (normalized.isNotEmpty()) {
                        if (it.style.noteId != null) {
                            add(
                                RinneBookContent.NoteReference(
                                    text = normalized,
                                    noteId = it.style.noteId,
                                )
                            )
                        } else {
                            addBodyChunkWithImplicitFootnotes(
                                text = normalized,
                                bold = it.style.bold,
                                italic = it.style.italic,
                                knownFootnotes = knownFootnotes,
                            )
                        }
                    }
                }
            }
            BlockRole.Epigraph -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Epigraph(text))
            }
            BlockRole.Cite -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Cite(text))
            }
            BlockRole.Poem -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.PoemLine(text))
            }
            BlockRole.TextAuthor -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.TextAuthor(text))
            }
            BlockRole.Date -> {
                val text = chunks.joinToString(separator = " ") { it.text }.normalizeFb2Text()
                if (text.isNotEmpty()) add(RinneBookContent.Date(text))
            }
        }
    }

    private fun MutableList<RinneBookContent>.appendImage(tag: XmlTag) {
        val refId = tag.getAttribute("l:href")
            ?: tag.getAttribute("xlink:href")
            ?: tag.getAttribute("href")
        val cleanRef = refId?.removePrefix("#")
        add(
            RinneBookContent.Image(
                refId = cleanRef,
                description = tag.getAttribute("alt") ?: tag.getAttribute("title"),
            )
        )
        add(RinneBookContent.LineBreak(2))
    }

    private fun MutableList<RinneBookContent>.appendTable(tag: XmlTag) {
        val rows = tag.collectTagsByName("tr")
            .map { row ->
                row.content
                    .filterIsInstance<XmlTagContent.Tag>()
                    .map { it.tag }
                    .flatMap { cellContainer ->
                        if (cellContainer.tag.equals("td", ignoreCase = true) || cellContainer.tag.equals("th", ignoreCase = true)) {
                            listOf(cellContainer)
                        } else {
                            cellContainer.collectTagsByName("td") + cellContainer.collectTagsByName("th")
                        }
                    }
                    .map { cell ->
                        cell.collectPlainText().normalizeFb2Text()
                    }
                    .filter { it.isNotBlank() }
            }
            .filter { it.isNotEmpty() }

        if (rows.isNotEmpty()) {
            add(RinneBookContent.Table(rows = rows))
            add(RinneBookContent.LineBreak(2))
        }
    }
}

private enum class BlockRole {
    Title,
    Subtitle,
    Body,
    Epigraph,
    Cite,
    Poem,
    TextAuthor,
    Date,
}

private data class InlineStyle(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val noteId: String? = null,
)

private data class InlineChunk(
    val text: String,
    val style: InlineStyle,
)

private fun collectInlineText(
    content: List<XmlTagContent>,
    inheritedStyle: InlineStyle,
): List<InlineChunk> {
    if (content.isEmpty()) return emptyList()

    val result = mutableListOf<InlineChunk>()
    content.forEach { item ->
        when (item) {
            is XmlTagContent.Text -> {
                val value = item.value.normalizeFb2Text()
                if (value.isNotEmpty()) {
                    result.add(InlineChunk(value, inheritedStyle))
                }
            }

            is XmlTagContent.Tag -> {
                val tag = item.tag.tag.lowercase()
                val nextStyle = when (tag) {
                    "strong", "b" -> inheritedStyle.copy(bold = true)
                    "emphasis", "i" -> inheritedStyle.copy(italic = true)
                    "a" -> {
                        val href = item.tag.getAttribute("l:href")
                            ?: item.tag.getAttribute("xlink:href")
                            ?: item.tag.getAttribute("href")
                        val type = item.tag.getAttribute("type")
                        val noteId = href?.removePrefix("#")
                        val isNoteRef = type.equals("note", ignoreCase = true) || noteId != null
                        if (isNoteRef) inheritedStyle.copy(noteId = noteId) else inheritedStyle
                    }
                    else -> inheritedStyle
                }
                result += collectInlineText(item.tag.content, nextStyle)
            }
        }
    }

    return result
}

private fun String.normalizeFb2Text(): String = trim()
    .replace("\\s+".toRegex(), " ")

private fun needsSpaceBetween(left: String, right: String): Boolean {
    if (left.isEmpty() || right.isEmpty()) return false

    val leftLast = left.last()
    val rightFirst = right.first()
    val rightIsPunctuation = rightFirst in setOf('.', ',', ';', ':', '!', '?', ')')
    val leftIsOpening = leftLast in setOf('(', '[', '"')
    if (rightIsPunctuation || leftIsOpening) return false

    return true
}

private fun XmlTag.getAttribute(name: String): String? {
    return attributes.firstOrNull { it.name.equals(name, ignoreCase = true) }?.value
}

private fun XmlTag.extractFootnotes(): Map<String, String> {
    val notes = mutableMapOf<String, String>()

    val noteBodies = content
        .filterIsInstance<XmlTagContent.Tag>()
        .map { it.tag }
        .filter { it.tag.equals("body", ignoreCase = true) }
        .filter { body -> body.getAttribute("name").equals("notes", ignoreCase = true) }

    noteBodies.forEach { body ->
        body.collectSectionsById(notes)
    }
    collectInlineFootnotesByPattern(notes)

    return notes
}

private fun XmlTag.collectSectionsById(target: MutableMap<String, String>) {
    val id = getAttribute("id")
    if (!id.isNullOrBlank()) {
        val text = collectPlainText().normalizeFb2Text()
        if (text.isNotBlank()) {
            target[id] = text
        }
    }

    content.forEach { child ->
        if (child is XmlTagContent.Tag) {
            child.tag.collectSectionsById(target)
        }
    }
}

private fun XmlTag.collectPlainText(): String {
    val builder = StringBuilder()

    content.forEach { child ->
        when (child) {
            is XmlTagContent.Text -> {
                val text = child.value.normalizeFb2Text()
                if (text.isNotBlank()) {
                    if (builder.isNotEmpty() && builder.last() != ' ') builder.append(' ')
                    builder.append(text)
                }
            }

            is XmlTagContent.Tag -> {
                val nested = child.tag.collectPlainText().normalizeFb2Text()
                if (nested.isNotBlank()) {
                    if (builder.isNotEmpty() && builder.last() != ' ') builder.append(' ')
                    builder.append(nested)
                }
            }
        }
    }

    return builder.toString()
}

private fun XmlTag.collectImageRefs(): List<String> {
    val result = mutableListOf<String>()
    content.forEach { child ->
        if (child is XmlTagContent.Tag) {
            val isImage = child.tag.tag.equals("image", ignoreCase = true)
            if (isImage) {
                val ref = child.tag.getAttribute("l:href")
                    ?: child.tag.getAttribute("xlink:href")
                    ?: child.tag.getAttribute("href")
                val cleanRef = ref?.removePrefix("#")
                if (!cleanRef.isNullOrBlank()) result.add(cleanRef)
            }
            result += child.tag.collectImageRefs()
        }
    }
    return result.distinct()
}

private fun XmlTag.collectTagsByName(tagName: String): List<XmlTag> {
    val result = mutableListOf<XmlTag>()
    content.forEach { child ->
        if (child is XmlTagContent.Tag) {
            if (child.tag.tag.equals(tagName, ignoreCase = true)) {
                result += child.tag
            }
            result += child.tag.collectTagsByName(tagName)
        }
    }
    return result
}

private fun XmlTag.extractImages(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    collectExternalImageLinks(result)
    return result
}

private fun XmlTag.collectExternalImageLinks(target: MutableMap<String, String>) {
    if (tag.equals("image", ignoreCase = true)) {
        val href = getAttribute("l:href")
            ?: getAttribute("xlink:href")
            ?: getAttribute("href")
        val refId = href?.removePrefix("#")
        val isExternal = href != null && !href.startsWith("#")
        if (!refId.isNullOrBlank() && isExternal) {
            target[refId] = href
        }
    }

    content.forEach { child ->
        if (child is XmlTagContent.Tag) {
            child.tag.collectExternalImageLinks(target)
        }
    }
}

private fun XmlTag.collectRawTextForBinary(): String {
    val builder = StringBuilder()
    content.forEach { child ->
        when (child) {
            is XmlTagContent.Text -> builder.append(child.value)
            is XmlTagContent.Tag -> builder.append(child.tag.collectRawTextForBinary())
        }
    }
    return builder.toString()
}

private fun XmlTag.extractImageBinaries(): Map<String, RinneBookBinaryImage> {
    val result = mutableMapOf<String, RinneBookBinaryImage>()
    collectBinaryImagesRaw(result)
    return result
}

private fun XmlTag.collectBinaryImagesRaw(target: MutableMap<String, RinneBookBinaryImage>) {
    if (tag.equals("binary", ignoreCase = true)) {
        val id = getAttribute("id")
        val contentType = getAttribute("content-type")
        if (!id.isNullOrBlank() && !contentType.isNullOrBlank()) {
            val base64 = collectRawTextForBinary()
                .replace("\\s+".toRegex(), "")
            if (base64.isNotBlank()) {
                target[id] = RinneBookBinaryImage(
                    contentType = contentType.trim(),
                    base64 = base64,
                )
            }
        }
    }

    content.forEach { child ->
        if (child is XmlTagContent.Tag) {
            child.tag.collectBinaryImagesRaw(target)
        }
    }
}

private fun XmlTag.extractImageCaptions(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    collectImageCaptions(result)
    return result
}

private fun XmlTag.collectImageCaptions(target: MutableMap<String, String>) {
    if (tag.equals("image", ignoreCase = true)) {
        val ref = getAttribute("l:href")
            ?: getAttribute("xlink:href")
            ?: getAttribute("href")
        val id = ref?.removePrefix("#")
        val caption = getAttribute("alt")
            ?: getAttribute("title")
            ?: getAttribute("description")
        if (!id.isNullOrBlank() && !caption.isNullOrBlank()) {
            target.putIfAbsent(id, caption.normalizeFb2Text())
        }
    }

    content.forEach { child ->
        if (child is XmlTagContent.Tag) {
            child.tag.collectImageCaptions(target)
        }
    }
}

private fun MutableList<RinneBookContent>.addBodyChunkWithImplicitFootnotes(
    text: String,
    bold: Boolean,
    italic: Boolean,
    knownFootnotes: Map<String, String>,
) {
    var cursor = 0
    noteReferenceRegex.findAll(text).forEach { match ->
        val start = match.range.first
        val endExclusive = match.range.last + 1

        if (start > cursor) {
            add(RinneBookContent.Text(text = text.substring(cursor, start), bold = bold, italic = italic))
        }

        val id = match.groupValues[1]
        if (knownFootnotes.containsKey(id)) {
            add(RinneBookContent.NoteReference(text = id, noteId = id))
        } else {
            add(RinneBookContent.Text(text = match.value, bold = bold, italic = italic))
        }

        cursor = endExclusive
    }

    if (cursor < text.length) {
        add(RinneBookContent.Text(text = text.substring(cursor), bold = bold, italic = italic))
    }
}

private fun XmlTag.collectInlineFootnotesByPattern(target: MutableMap<String, String>) {
    if (tag.equals("p", ignoreCase = true)) {
        val paragraph = collectPlainText().normalizeFb2Text()
        val match = footnoteDefinitionRegex.matchEntire(paragraph)
        if (match != null) {
            val id = match.groupValues[1]
            val text = match.groupValues[2].normalizeFb2Text()
            if (text.isNotBlank()) {
                target.putIfAbsent(id, text)
            }
        }
    }

    content.forEach { child ->
        if (child is XmlTagContent.Tag) {
            child.tag.collectInlineFootnotesByPattern(target)
        }
    }
}

private fun extractFootnoteDefinitionId(text: String): String? {
    return footnoteDefinitionRegex.matchEntire(text)?.groupValues?.getOrNull(1)
}

private fun String.isFootnoteHeading(): Boolean {
    return lowercase().contains("anmerkungen")
}

private val footnoteDefinitionRegex = Regex("^\\((\\d{1,3})\\)\\s+(.+)$")
private val noteReferenceRegex = Regex("\\((\\d{1,3})\\)")