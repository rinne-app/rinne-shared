package com.rinne.shared.network.model.notes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A single broken rule, reported back to the client as part of a `422` response so it can point at
 * the offending part of the document without parsing prose.
 *
 * [field] is a JSON-pointer-like path into the rejected payload, e.g.
 * `content.blocks[3].spans[1].start`.
 */
@Serializable
data class NetworkNoteViolation(
    @SerialName("field") val field: String,
    @SerialName("rule") val rule: NetworkNoteRule,
)

@Serializable
enum class NetworkNoteRule {
    @SerialName("unsupportedVersion")
    UNSUPPORTED_VERSION,

    @SerialName("missingField")
    MISSING_FIELD,

    @SerialName("unexpectedField")
    UNEXPECTED_FIELD,

    @SerialName("outOfRange")
    OUT_OF_RANGE,

    @SerialName("invalidRange")
    INVALID_RANGE,

    @SerialName("splitsSurrogatePair")
    SPLITS_SURROGATE_PAIR,

    @SerialName("overlappingInlineCode")
    OVERLAPPING_INLINE_CODE,

    @SerialName("invalidUrl")
    INVALID_URL,

    @SerialName("duplicateId")
    DUPLICATE_ID,

    @SerialName("blankId")
    BLANK_ID,

    @SerialName("limitExceeded")
    LIMIT_EXCEEDED,
}

/**
 * Validates a version-1 [NetworkRichTextDocument] against the rules in the notes backend contract.
 *
 * Lives in the shared module so the backend validator and the clients agree byte-for-byte on what a
 * valid document is; the backend is still the authority that rejects a write.
 */
object NetworkRichTextValidator {

    /** Returns every rule the [document] breaks, or an empty list when it is valid. */
    fun validate(
        document: NetworkRichTextDocument,
        path: String = "content",
    ): List<NetworkNoteViolation> = buildList {
        if (document.version != NetworkRichTextDocument.CURRENT_VERSION) {
            add(NetworkNoteViolation("$path.version", NetworkNoteRule.UNSUPPORTED_VERSION))
            // A document of an unknown version is preserved, not inspected with version-1 rules.
            return@buildList
        }

        validateDocumentLimits(document, path)

        val seenIds = mutableSetOf<String>()
        document.blocks.forEachIndexed { index, block ->
            val blockPath = "$path.blocks[$index]"
            validateBlockId(block, blockPath, seenIds)
            validateBlockFields(block, blockPath)
            validateSpans(block, blockPath)
        }
    }

    private fun MutableList<NetworkNoteViolation>.validateDocumentLimits(
        document: NetworkRichTextDocument,
        path: String,
    ) {
        if (document.blocks.size > NetworkRichTextLimits.MAX_BLOCKS) {
            add(NetworkNoteViolation("$path.blocks", NetworkNoteRule.LIMIT_EXCEEDED))
        }
        if (document.blocks.sumOf { it.text.length } > NetworkRichTextLimits.MAX_TEXT_LENGTH) {
            add(NetworkNoteViolation("$path.blocks", NetworkNoteRule.LIMIT_EXCEEDED))
        }
        if (document.blocks.sumOf { it.spans.size } > NetworkRichTextLimits.MAX_SPANS) {
            add(NetworkNoteViolation("$path.blocks", NetworkNoteRule.LIMIT_EXCEEDED))
        }
    }

    private fun MutableList<NetworkNoteViolation>.validateBlockId(
        block: NetworkRichTextBlock,
        blockPath: String,
        seenIds: MutableSet<String>,
    ) {
        if (block.id.isBlank()) {
            add(NetworkNoteViolation("$blockPath.id", NetworkNoteRule.BLANK_ID))
        } else if (!seenIds.add(block.id)) {
            add(NetworkNoteViolation("$blockPath.id", NetworkNoteRule.DUPLICATE_ID))
        }
    }

    private fun MutableList<NetworkNoteViolation>.validateBlockFields(
        block: NetworkRichTextBlock,
        blockPath: String,
    ) {
        val requiresLevel = block.type == NetworkRichTextBlockType.HEADING
        val requiresChecked = block.type == NetworkRichTextBlockType.CHECKLIST_ITEM

        when {
            requiresLevel && block.level == null ->
                add(NetworkNoteViolation("$blockPath.level", NetworkNoteRule.MISSING_FIELD))

            requiresLevel && block.level !in NetworkRichTextLimits.HEADING_LEVELS ->
                add(NetworkNoteViolation("$blockPath.level", NetworkNoteRule.OUT_OF_RANGE))

            !requiresLevel && block.level != null ->
                add(NetworkNoteViolation("$blockPath.level", NetworkNoteRule.UNEXPECTED_FIELD))
        }

        when {
            requiresChecked && block.checked == null ->
                add(NetworkNoteViolation("$blockPath.checked", NetworkNoteRule.MISSING_FIELD))

            !requiresChecked && block.checked != null ->
                add(NetworkNoteViolation("$blockPath.checked", NetworkNoteRule.UNEXPECTED_FIELD))
        }

        if (block.type == NetworkRichTextBlockType.DIVIDER) {
            if (block.text.isNotEmpty()) {
                add(NetworkNoteViolation("$blockPath.text", NetworkNoteRule.UNEXPECTED_FIELD))
            }
            if (block.spans.isNotEmpty()) {
                add(NetworkNoteViolation("$blockPath.spans", NetworkNoteRule.UNEXPECTED_FIELD))
            }
        }
    }

    private fun MutableList<NetworkNoteViolation>.validateSpans(
        block: NetworkRichTextBlock,
        blockPath: String,
    ) {
        block.spans.forEachIndexed { index, span ->
            val spanPath = "$blockPath.spans[$index]"

            if (span.start < 0 || span.end > block.text.length || span.start >= span.end) {
                add(NetworkNoteViolation("$spanPath.start", NetworkNoteRule.INVALID_RANGE))
                return@forEachIndexed
            }
            if (block.text.splitsSurrogatePairAt(span.start)) {
                add(NetworkNoteViolation("$spanPath.start", NetworkNoteRule.SPLITS_SURROGATE_PAIR))
            }
            if (block.text.splitsSurrogatePairAt(span.end)) {
                add(NetworkNoteViolation("$spanPath.end", NetworkNoteRule.SPLITS_SURROGATE_PAIR))
            }

            val isLink = span.type == NetworkRichTextSpanType.LINK
            when {
                isLink && span.url == null ->
                    add(NetworkNoteViolation("$spanPath.url", NetworkNoteRule.MISSING_FIELD))

                isLink && !isSupportedLinkUrl(span.url) ->
                    add(NetworkNoteViolation("$spanPath.url", NetworkNoteRule.INVALID_URL))

                !isLink && span.url != null ->
                    add(NetworkNoteViolation("$spanPath.url", NetworkNoteRule.UNEXPECTED_FIELD))
            }

            if (span.type == NetworkRichTextSpanType.INLINE_CODE &&
                block.spans.anyOverlapsExcept(span, index)
            ) {
                add(NetworkNoteViolation(spanPath, NetworkNoteRule.OVERLAPPING_INLINE_CODE))
            }
        }
    }

    private fun List<NetworkRichTextSpan>.anyOverlapsExcept(
        span: NetworkRichTextSpan,
        index: Int,
    ): Boolean = withIndex().any { (otherIndex, other) ->
        otherIndex != index && span.start < other.end && other.start < span.end
    }

    /** True when [offset] falls between the halves of a UTF-16 surrogate pair. */
    private fun String.splitsSurrogatePairAt(offset: Int): Boolean =
        offset in 1..lastIndex && this[offset - 1].isHighSurrogate() && this[offset].isLowSurrogate()
}

/**
 * An absolute `http`/`https` URL, the only link target a version-1 document accepts.
 *
 * Shared so the editor's link input, the backend validator, and the clients all apply one rule.
 */
fun isSupportedLinkUrl(url: String?): Boolean {
    if (url.isNullOrBlank()) return false
    val scheme = url.substringBefore("://", missingDelimiterValue = "").lowercase()
    return (scheme == "http" || scheme == "https") && url.substringAfter("://").isNotBlank()
}

/**
 * Derives the plain-text body of a document by joining non-divider block text with line breaks and
 * dropping every span.
 *
 * This is what the backend stores for search and returns as the note preview; it is derived, never
 * accepted as client input.
 */
fun NetworkRichTextDocument.derivePlainText(): String = blocks
    .filter { it.type != NetworkRichTextBlockType.DIVIDER }
    .joinToString(separator = "\n") { it.text }

/** [derivePlainText] truncated to [maxLength] UTF-16 code units, for list responses. */
fun NetworkRichTextDocument.derivePlainTextPreview(maxLength: Int): String {
    val plainText = derivePlainText()
    if (plainText.length <= maxLength) return plainText

    // Never cut between the halves of a surrogate pair.
    val end = if (plainText[maxLength - 1].isHighSurrogate()) maxLength - 1 else maxLength
    return plainText.substring(0, end)
}

/** Wraps legacy plain [text] into a version-1 document holding a single paragraph. */
fun legacyNetworkRichTextDocument(text: String, blockId: String): NetworkRichTextDocument =
    NetworkRichTextDocument(
        blocks = listOf(
            NetworkRichTextBlock(
                id = blockId,
                type = NetworkRichTextBlockType.PARAGRAPH,
                text = text,
            ),
        ),
    )
