package com.rinne.libraries.reader.markdown.deprecated.markdown.annotator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.LocalMarkdownAnnotator
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.LocalMarkdownTypography
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.LocalReferenceLinkHandler
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownAnnotator
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.ReferenceLinkHandler
import com.rinne.libraries.reader.markdown.deprecated.markdown.utils.codeSpanStyle

@Stable
interface AnnotatorSettings {
    /** Represents the [TextLinkStyles] applied onto links within this annotated string */
    val linkTextSpanStyle: TextLinkStyles

    /** Represents the [SpanStyle] applied onto code inline blocks within this annotated string */
    val codeSpanStyle: SpanStyle

    /** Provides custom interception logic for building this annotated string */
    val annotator: MarkdownAnnotator

    /** Represents the [ReferenceLinkHandler] used to store and find links when clicks on links occur. */
    val referenceLinkHandler: ReferenceLinkHandler?

    /** Represents the [LinkInteractionListener] used for links in this annotated string */
    val linkInteractionListener: LinkInteractionListener?
}

@Immutable
class DefaultAnnotatorSettings(
    override val linkTextSpanStyle: TextLinkStyles,
    override val codeSpanStyle: SpanStyle,
    override val annotator: MarkdownAnnotator,
    override val referenceLinkHandler: ReferenceLinkHandler? = null,
    override val linkInteractionListener: LinkInteractionListener? = null,
) : AnnotatorSettings

@Composable
fun annotatorSettings(
    linkTextSpanStyle: TextLinkStyles = LocalMarkdownTypography.current.textLink,
    codeSpanStyle: SpanStyle = LocalMarkdownTypography.current.codeSpanStyle,
    annotator: MarkdownAnnotator = LocalMarkdownAnnotator.current,
    referenceLinkHandler: ReferenceLinkHandler? = LocalReferenceLinkHandler.current,
    uriHandler: UriHandler = LocalUriHandler.current,
    linkInteractionListener: LinkInteractionListener? = LinkInteractionListener { link ->
        val annotationUrl = (link as? LinkAnnotation.Url)?.url
        if (annotationUrl != null) {
            val foundReference = referenceLinkHandler?.find(annotationUrl)?.takeIf { it.isNotEmpty() } ?: annotationUrl
            // wait for finger up to navigate to the link
            try {
                uriHandler.openUri(foundReference)
            } catch (t: Throwable) {
                println("Could not open the provided url: $foundReference // ${t.message}")
            }
        }
    },
): AnnotatorSettings = DefaultAnnotatorSettings(
    linkTextSpanStyle = linkTextSpanStyle,
    codeSpanStyle = codeSpanStyle,
    annotator = annotator,
    referenceLinkHandler = referenceLinkHandler,
    linkInteractionListener = linkInteractionListener,
)