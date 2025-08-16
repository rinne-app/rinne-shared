package com.rinne.libraries.reader.markdown.deprecated.markdown.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import org.intellij.markdown.ast.ASTNode

interface MarkdownAnnotator {

    /**
     * Use the [AnnotatedString.Builder] to build the string to display.
     * Return `true` to consume the child, false to allow default handling.
     *
     * @param content contains the whole content, and requires the `child` [ASTNode] to extract relevant text.
     */
    val annotate: (AnnotatedString.Builder.(content: String, child: ASTNode) -> Boolean)?

    /** Defines static configuration for the [AnnotatedString] annotator */
    val config: MarkdownAnnotatorConfig
}

@Immutable
class DefaultMarkdownAnnotator(
    override val annotate: (AnnotatedString.Builder.(content: String, child: ASTNode) -> Boolean)?,
    override val config: MarkdownAnnotatorConfig,
) : MarkdownAnnotator

fun markdownAnnotator(
    config: MarkdownAnnotatorConfig = markdownAnnotatorConfig(),
    annotate: (AnnotatedString.Builder.(content: String, child: ASTNode) -> Boolean)? = null,
): MarkdownAnnotator = DefaultMarkdownAnnotator(
    annotate = annotate,
    config = config
)
