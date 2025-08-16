package com.rinne.libraries.reader.markdown.deprecated.elements

import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownCheckBox
import org.intellij.markdown.ast.ASTNode

@Composable
fun MarkdownCheckBox(
    content: String,
    node: ASTNode,
    style: TextStyle,
) = MarkdownCheckBox(
    content = content,
    node = node,
    style = style,
    checkedIndicator = { checked, modifier ->
        Checkbox(checked = checked, onCheckedChange = null, modifier = modifier)
    },
)