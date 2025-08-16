package com.rinne.libraries.text.highlights.impl.parser

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.rinne.libraries.text.highlights.TextHighlightsTheme
import com.rinne.libraries.text.highlights.impl.model.RinneAnnotationAttribute
import com.rinne.libraries.text.highlights.impl.type.RinneAnnotationsColor
import com.rinne.libraries.text.highlights.impl.type.RinneAnnotationsFontWeight

internal object FontSizeSpanStyleParser : SpanStyleParser<RinneAnnotationAttribute.FontSize> {

    override fun parse(
        attribute: RinneAnnotationAttribute.FontSize,
        theme: TextHighlightsTheme,
    ): SpanStyle {
        return SpanStyle(fontSize = attribute.fontSize)
    }
}