package com.rinne.libraries.reader.markdown.deprecated.markdown.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.components.MarkdownComponents
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.components.markdownComponents
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.BulletHandler
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.DefaultMarkdownAnnotator
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.DefaultMarkdownAnnotatorConfig
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.DefaultMarkdownExtendedSpans
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.DefaultMarkdownInlineContent
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.ImageTransformer
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownAnimations
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownAnnotator
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownColors
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownDimens
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownExtendedSpans
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownInlineContent
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownPadding
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownTypography
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.ReferenceLinkHandler

/**
 * The CompositionLocal to provide functionality related to transforming the bullet of an ordered list
 */
val LocalBulletListHandler = staticCompositionLocalOf {
    return@staticCompositionLocalOf BulletHandler { _, _, _, _, _ -> "• " }
}

/**
 * The CompositionLocal to provide functionality related to transforming the bullet of an ordered list
 */
val LocalOrderedListHandler = staticCompositionLocalOf {
    return@staticCompositionLocalOf BulletHandler { _, _, index, listNumber, _ -> "${listNumber + index}. " }
}

/**
 * Local [ReferenceLinkHandler] provider
 */
val LocalReferenceLinkHandler = staticCompositionLocalOf<ReferenceLinkHandler> {
    error("CompositionLocal ReferenceLinkHandler not present")
}

/**
 * Local [MarkdownColors] provider
 */
val LocalMarkdownColors = compositionLocalOf<MarkdownColors> {
    error("No local MarkdownColors")
}

/**
 * Local [MarkdownTypography] provider
 */
val LocalMarkdownTypography = compositionLocalOf<MarkdownTypography> {
    error("No local MarkdownTypography")
}

/**
 * Local [MarkdownPadding] provider
 */
val LocalMarkdownPadding = staticCompositionLocalOf<MarkdownPadding> {
    error("No local Padding")
}

/**
 * Local [MarkdownDimens] provider
 */
val LocalMarkdownDimens = compositionLocalOf<MarkdownDimens> {
    error("No local MarkdownDimens")
}

/**
 * Local [ImageTransformer] provider
 */
val LocalImageTransformer = staticCompositionLocalOf<ImageTransformer> {
    error("No local ImageTransformer")
}

/**
 * Local [MarkdownInlineContent] provider
 */
val LocalMarkdownInlineContent = staticCompositionLocalOf<MarkdownInlineContent> {
    return@staticCompositionLocalOf DefaultMarkdownInlineContent(mapOf())
}

/**
 * Local [MarkdownAnnotator] provider
 */
val LocalMarkdownAnnotator = compositionLocalOf<MarkdownAnnotator> {
    return@compositionLocalOf DefaultMarkdownAnnotator(null, DefaultMarkdownAnnotatorConfig())
}

/**
 * Local [MarkdownExtendedSpans] provider
 */
val LocalMarkdownExtendedSpans = compositionLocalOf<MarkdownExtendedSpans> {
    return@compositionLocalOf DefaultMarkdownExtendedSpans(null)
}

/**
 * Local [MarkdownComponents] provider
 */
val LocalMarkdownComponents = compositionLocalOf<MarkdownComponents> {
    return@compositionLocalOf markdownComponents()
}

/**
 * Local [MarkdownAnimations] provider
 */
val LocalMarkdownAnimations = compositionLocalOf<MarkdownAnimations> {
    error("No local MarkdownAnimations")
}