package com.rinne.libraries.text.highlights.impl.parser

import androidx.compose.ui.text.SpanStyle
import com.rinne.libraries.text.highlights.TextHighlightsTheme
import com.rinne.libraries.text.highlights.impl.model.RinneAnnotationAttribute

internal interface SpanStyleParser<T : RinneAnnotationAttribute> {
    fun parse(attribute: T, theme: TextHighlightsTheme): SpanStyle

    companion object {
        fun parse(attribute: RinneAnnotationAttribute, theme: TextHighlightsTheme): SpanStyle {
            return when (attribute) {
                is RinneAnnotationAttribute.Color ->
                    ColorSpanStyleParser.parse(attribute, theme)

                is RinneAnnotationAttribute.FontSize ->
                    FontSizeSpanStyleParser.parse(attribute, theme)

                is RinneAnnotationAttribute.FontWeight ->
                    FontWeightSpanStyleParser.parse(attribute, theme)
            }
        }
    }
}