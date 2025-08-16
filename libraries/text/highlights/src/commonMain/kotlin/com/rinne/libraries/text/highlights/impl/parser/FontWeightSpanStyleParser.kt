package com.rinne.libraries.text.highlights.impl.parser

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.rinne.libraries.text.highlights.TextHighlightsTheme
import com.rinne.libraries.text.highlights.impl.model.RinneAnnotationAttribute
import com.rinne.libraries.text.highlights.impl.type.RinneAnnotationsColor
import com.rinne.libraries.text.highlights.impl.type.RinneAnnotationsFontWeight

internal object FontWeightSpanStyleParser : SpanStyleParser<RinneAnnotationAttribute.FontWeight> {

    override fun parse(
        attribute: RinneAnnotationAttribute.FontWeight,
        theme: TextHighlightsTheme,
    ): SpanStyle {
        return SpanStyle(
            fontWeight = when (attribute.fontWeight) {
                RinneAnnotationsFontWeight.MEDIUM -> FontWeight.Medium
                RinneAnnotationsFontWeight.SEMIBOLD -> FontWeight.SemiBold
                RinneAnnotationsFontWeight.BOLD -> FontWeight.Bold
            }
        )
    }
}