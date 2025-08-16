package com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.rinne.libraries.reader.markdown.deprecated.markdown.annotator.AnnotatorSettings
import com.rinne.libraries.reader.markdown.deprecated.markdown.annotator.annotatorSettings
import com.rinne.libraries.reader.markdown.deprecated.markdown.annotator.buildMarkdownAnnotatedString
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.LocalMarkdownTypography
import org.intellij.markdown.ast.ASTNode

@Composable
fun MarkdownParagraph(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.paragraph,
    annotatorSettings: AnnotatorSettings = annotatorSettings(),
) {
    val styledText = buildAnnotatedString {
        pushStyle(style.toSpanStyle())
        buildMarkdownAnnotatedString(content = content, node = node, annotatorSettings = annotatorSettings)
        pop()
    }

    MarkdownText(
        styledText,
        modifier = modifier,
        style = style,
    )
}
