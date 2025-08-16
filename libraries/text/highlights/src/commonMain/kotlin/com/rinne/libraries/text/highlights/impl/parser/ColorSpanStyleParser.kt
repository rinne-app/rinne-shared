package com.rinne.libraries.text.highlights.impl.parser

import androidx.compose.ui.text.SpanStyle
import com.rinne.libraries.text.highlights.TextHighlightsTheme
import com.rinne.libraries.text.highlights.impl.model.RinneAnnotationAttribute
import com.rinne.libraries.text.highlights.impl.type.RinneAnnotationsColor

internal object ColorSpanStyleParser : SpanStyleParser<RinneAnnotationAttribute.Color> {

    override fun parse(
        attribute: RinneAnnotationAttribute.Color,
        theme: TextHighlightsTheme,
    ): SpanStyle {
        return SpanStyle(
            color = when (attribute.color) {
                RinneAnnotationsColor.ERROR -> theme.colorScheme.error
                RinneAnnotationsColor.PRIMARY -> theme.colorScheme.primary
            }
        )
    }
}