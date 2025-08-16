package com.rinne.libraries.text.highlights.impl

import androidx.compose.ui.text.AnnotatedString
import com.rinne.libraries.text.highlights.TextHighlightsTheme
import com.rinne.libraries.text.highlights.impl.model.RinneAnnotation
import com.rinne.libraries.text.highlights.impl.parser.SpanStyleParser

internal fun AnnotatedString.Builder.applyAnnotation(
    annotation: RinneAnnotation,
    theme: TextHighlightsTheme,
) {
    addStyle(
        start = annotation.start,
        style = SpanStyleParser.parse(annotation.attribute, theme),
        end = annotation.end
    )
}