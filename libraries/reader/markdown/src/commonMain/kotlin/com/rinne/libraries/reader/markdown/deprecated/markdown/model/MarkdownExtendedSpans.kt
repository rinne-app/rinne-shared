package com.rinne.libraries.reader.markdown.deprecated.markdown.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.extendedspans.ExtendedSpans

interface MarkdownExtendedSpans {
    val extendedSpans: (@Composable () -> ExtendedSpans)?
}

@Immutable
class DefaultMarkdownExtendedSpans(
    override val extendedSpans: (@Composable () -> ExtendedSpans)?
) : MarkdownExtendedSpans

@Composable
fun markdownExtendedSpans(
    extendedSpans: (@Composable () -> ExtendedSpans)? = null
): MarkdownExtendedSpans = DefaultMarkdownExtendedSpans(extendedSpans)
