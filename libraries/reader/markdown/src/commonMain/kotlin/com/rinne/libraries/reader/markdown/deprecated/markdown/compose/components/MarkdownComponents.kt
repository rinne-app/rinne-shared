package com.rinne.libraries.reader.markdown.deprecated.markdown.compose.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.LocalReferenceLinkHandler
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownBlockQuote
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownBulletList
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownCheckBox
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownCodeBlock
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownCodeFence
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownDivider
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownHeader
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownImage
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownOrderedList
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownParagraph
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownTable
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.MarkdownText
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.elements.listDepth
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownTypography
import com.rinne.libraries.reader.markdown.deprecated.markdown.utils.getUnescapedTextInNode
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType

typealias MarkdownComponent = @Composable (MarkdownComponentModel) -> Unit

typealias CustomMarkdownComponent = @Composable (IElementType, MarkdownComponentModel) -> Unit

/**
 * Model holding data relevant for a component
 */
@Stable
data class MarkdownComponentModel(
    val content: String,
    val node: ASTNode,
    val typography: MarkdownTypography,
    val extra: ImmutableMap<String, Any> = persistentMapOf(),
)

private fun MarkdownComponentModel.getUnescapedTextInNode() = node.getUnescapedTextInNode(content)

fun markdownComponents(
    text: MarkdownComponent = CurrentComponentsBridge.text,
    eol: MarkdownComponent = CurrentComponentsBridge.eol,
    codeFence: MarkdownComponent = CurrentComponentsBridge.codeFence,
    codeBlock: MarkdownComponent = CurrentComponentsBridge.codeBlock,
    heading1: MarkdownComponent = CurrentComponentsBridge.heading1,
    heading2: MarkdownComponent = CurrentComponentsBridge.heading2,
    heading3: MarkdownComponent = CurrentComponentsBridge.heading3,
    heading4: MarkdownComponent = CurrentComponentsBridge.heading4,
    heading5: MarkdownComponent = CurrentComponentsBridge.heading5,
    heading6: MarkdownComponent = CurrentComponentsBridge.heading6,
    setextHeading1: MarkdownComponent = CurrentComponentsBridge.setextHeading1,
    setextHeading2: MarkdownComponent = CurrentComponentsBridge.setextHeading2,
    blockQuote: MarkdownComponent = CurrentComponentsBridge.blockQuote,
    paragraph: MarkdownComponent = CurrentComponentsBridge.paragraph,
    orderedList: MarkdownComponent = CurrentComponentsBridge.orderedList,
    unorderedList: MarkdownComponent = CurrentComponentsBridge.unorderedList,
    image: MarkdownComponent = CurrentComponentsBridge.image,
    linkDefinition: MarkdownComponent = {
        @Suppress("DEPRECATION")
        CurrentComponentsBridge.linkDefinition
    },
    horizontalRule: MarkdownComponent = CurrentComponentsBridge.horizontalRule,
    table: MarkdownComponent = CurrentComponentsBridge.table,
    checkbox: MarkdownComponent = CurrentComponentsBridge.checkbox,
    custom: CustomMarkdownComponent? = CurrentComponentsBridge.custom,
): MarkdownComponents = DefaultMarkdownComponents(
    text = text,
    eol = eol,
    codeFence = codeFence,
    codeBlock = codeBlock,
    heading1 = heading1,
    heading2 = heading2,
    heading3 = heading3,
    heading4 = heading4,
    heading5 = heading5,
    heading6 = heading6,
    setextHeading1 = setextHeading1,
    setextHeading2 = setextHeading2,
    blockQuote = blockQuote,
    paragraph = paragraph,
    orderedList = orderedList,
    unorderedList = unorderedList,
    image = image,
    linkDefinition = linkDefinition,
    horizontalRule = horizontalRule,
    table = table,
    checkbox = checkbox,
    custom = custom,
)

/**
 * Interface defining all supported components.
 */
@Stable
interface MarkdownComponents {
    val text: MarkdownComponent
    val eol: MarkdownComponent
    val codeFence: MarkdownComponent
    val codeBlock: MarkdownComponent
    val heading1: MarkdownComponent
    val heading2: MarkdownComponent
    val heading3: MarkdownComponent
    val heading4: MarkdownComponent
    val heading5: MarkdownComponent
    val heading6: MarkdownComponent
    val setextHeading1: MarkdownComponent
    val setextHeading2: MarkdownComponent
    val blockQuote: MarkdownComponent
    val paragraph: MarkdownComponent
    val orderedList: MarkdownComponent
    val unorderedList: MarkdownComponent
    val image: MarkdownComponent

    @Deprecated("The lookup of link definitions is now handled by the parser. It is advised to use the new API. This will be removed in a future version.")
    val linkDefinition: MarkdownComponent
    val horizontalRule: MarkdownComponent
    val table: MarkdownComponent
    val checkbox: MarkdownComponent
    val custom: CustomMarkdownComponent?
}

private class DefaultMarkdownComponents(
    override val text: MarkdownComponent,
    override val eol: MarkdownComponent,
    override val codeFence: MarkdownComponent,
    override val codeBlock: MarkdownComponent,
    override val heading1: MarkdownComponent,
    override val heading2: MarkdownComponent,
    override val heading3: MarkdownComponent,
    override val heading4: MarkdownComponent,
    override val heading5: MarkdownComponent,
    override val heading6: MarkdownComponent,
    override val setextHeading1: MarkdownComponent,
    override val setextHeading2: MarkdownComponent,
    override val blockQuote: MarkdownComponent,
    override val paragraph: MarkdownComponent,
    override val orderedList: MarkdownComponent,
    override val unorderedList: MarkdownComponent,
    override val image: MarkdownComponent,
    @Deprecated("The lookup of link definitions is now handled by the parser. It is advised to use the new API. This will be removed in a future version.")
    override val linkDefinition: MarkdownComponent,
    override val horizontalRule: MarkdownComponent,
    override val table: MarkdownComponent,
    override val checkbox: MarkdownComponent,
    override val custom: CustomMarkdownComponent?,
) : MarkdownComponents

/**
 * Adapts the universal signature @Composable (MarkdownComponentModel) -> Unit to the existing components.
 */
object CurrentComponentsBridge {
    val text: MarkdownComponent = {
        MarkdownText(it.getUnescapedTextInNode(), style = it.typography.text)
    }
    val eol: MarkdownComponent = { }
    val codeFence: MarkdownComponent = {
        MarkdownCodeFence(it.content, it.node, style = it.typography.code)
    }
    val codeBlock: MarkdownComponent = {
        MarkdownCodeBlock(it.content, it.node, style = it.typography.code)
    }
    val heading1: MarkdownComponent = {
        MarkdownHeader(it.content, it.node, style = it.typography.h1)
    }
    val heading2: MarkdownComponent = {
        MarkdownHeader(it.content, it.node, style = it.typography.h2)
    }
    val heading3: MarkdownComponent = {
        MarkdownHeader(it.content, it.node, style = it.typography.h3)
    }
    val heading4: MarkdownComponent = {
        MarkdownHeader(it.content, it.node, style = it.typography.h4)
    }
    val heading5: MarkdownComponent = {
        MarkdownHeader(it.content, it.node, style = it.typography.h5)
    }
    val heading6: MarkdownComponent = {
        MarkdownHeader(it.content, it.node, style = it.typography.h6)
    }
    val setextHeading1: MarkdownComponent = {
        MarkdownHeader(it.content, it.node, style = it.typography.h1, contentChildType = MarkdownTokenTypes.SETEXT_CONTENT)
    }
    val setextHeading2: MarkdownComponent = {
        MarkdownHeader(it.content, it.node, style = it.typography.h2, contentChildType = MarkdownTokenTypes.SETEXT_CONTENT)
    }
    val blockQuote: MarkdownComponent = {
        MarkdownBlockQuote(it.content, it.node, style = it.typography.quote)
    }
    val paragraph: MarkdownComponent = {
        MarkdownParagraph(it.content, it.node, style = it.typography.paragraph)
    }
    val orderedList: MarkdownComponent = {
        MarkdownOrderedList(it.content, it.node, style = it.typography.ordered, it.listDepth)
    }
    val unorderedList: MarkdownComponent = {
        MarkdownBulletList(it.content, it.node, style = it.typography.bullet, it.listDepth)
    }
    val image: MarkdownComponent = {
        MarkdownImage(it.content, it.node)
    }

    @Deprecated("The lookup of link definitions is now handled by the parser. It is advised to use the new API. This will be removed in a future version.")
    val linkDefinition: MarkdownComponent = {
        val linkLabel = it.node.findChildOfType(MarkdownElementTypes.LINK_LABEL)?.getUnescapedTextInNode(it.content)
        if (linkLabel != null) {
            val destination = it.node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getUnescapedTextInNode(it.content)
            LocalReferenceLinkHandler.current.store(linkLabel, destination)
        }
    }

    val horizontalRule: MarkdownComponent = {
        MarkdownDivider(Modifier.fillMaxWidth())
    }
    val table: MarkdownComponent = {
        MarkdownTable(it.content, it.node, style = it.typography.table)
    }
    val checkbox: MarkdownComponent = {
        MarkdownCheckBox(it.content, it.node, style = it.typography.text)
    }
    val custom: CustomMarkdownComponent? = null
}
