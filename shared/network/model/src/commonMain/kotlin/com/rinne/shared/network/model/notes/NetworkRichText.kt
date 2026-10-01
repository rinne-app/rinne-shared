package com.rinne.shared.network.model.notes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Versioned, platform-neutral rich-text document used as the note body wire format.
 *
 * The schema is the compatibility boundary between clients and the backend: Compose editor state
 * never crosses this layer. A client that does not understand a future [version] must preserve the
 * document as-is instead of overwriting it with a partial conversion.
 */
@Serializable
data class NetworkRichTextDocument(
    @SerialName("version") val version: Int = CURRENT_VERSION,
    @SerialName("blocks") val blocks: List<NetworkRichTextBlock> = emptyList(),
) {
    companion object {
        const val CURRENT_VERSION: Int = 1

        val Empty = NetworkRichTextDocument()
    }
}

/**
 * A single block of a [NetworkRichTextDocument].
 *
 * The wire shape is intentionally flat: [type] selects which of the optional block-specific fields
 * are meaningful. [level] is required for [NetworkRichTextBlockType.HEADING] and [checked] for
 * [NetworkRichTextBlockType.CHECKLIST_ITEM]; every other block rejects them.
 */
@Serializable
data class NetworkRichTextBlock(
    @SerialName("id") val id: String,
    @SerialName("type") val type: NetworkRichTextBlockType,
    @SerialName("text") val text: String = "",
    @SerialName("spans") val spans: List<NetworkRichTextSpan> = emptyList(),
    @SerialName("level") val level: Int? = null,
    @SerialName("checked") val checked: Boolean? = null,
)

@Serializable
enum class NetworkRichTextBlockType {
    @SerialName("paragraph")
    PARAGRAPH,

    @SerialName("heading")
    HEADING,

    @SerialName("bulletListItem")
    BULLET_LIST_ITEM,

    @SerialName("numberedListItem")
    NUMBERED_LIST_ITEM,

    @SerialName("checklistItem")
    CHECKLIST_ITEM,

    @SerialName("quote")
    QUOTE,

    @SerialName("codeBlock")
    CODE_BLOCK,

    @SerialName("divider")
    DIVIDER,
}

/**
 * An inline style applied to `[start, end)` of the enclosing block's text.
 *
 * Offsets are zero-based UTF-16 code units, matching Compose editing offsets. [url] is required for
 * [NetworkRichTextSpanType.LINK] and rejected for every other span type.
 */
@Serializable
data class NetworkRichTextSpan(
    @SerialName("start") val start: Int,
    @SerialName("end") val end: Int,
    @SerialName("type") val type: NetworkRichTextSpanType,
    @SerialName("url") val url: String? = null,
)

@Serializable
enum class NetworkRichTextSpanType {
    @SerialName("bold")
    BOLD,

    @SerialName("italic")
    ITALIC,

    @SerialName("underline")
    UNDERLINE,

    @SerialName("strikethrough")
    STRIKETHROUGH,

    @SerialName("highlight")
    HIGHLIGHT,

    @SerialName("inlineCode")
    INLINE_CODE,

    @SerialName("link")
    LINK,
}

/**
 * Safety limits for a version-1 document, shared by the backend validator and the clients so both
 * enforce one set of numbers.
 */
object NetworkRichTextLimits {
    const val MAX_BLOCKS: Int = 20_000
    const val MAX_TEXT_LENGTH: Int = 1_000_000
    const val MAX_SPANS: Int = 100_000
    const val MAX_TITLE_LENGTH: Int = 255
    const val MAX_FOLDER_NAME_LENGTH: Int = 255

    val HEADING_LEVELS: IntRange = 1..3
}
