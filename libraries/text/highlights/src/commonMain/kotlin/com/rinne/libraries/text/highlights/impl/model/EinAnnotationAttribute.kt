package com.rinne.libraries.text.highlights.impl.model

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.rinne.libraries.text.highlights.impl.type.RinneAnnotationsColor
import com.rinne.libraries.text.highlights.impl.type.RinneAnnotationsFontWeight

internal sealed interface RinneAnnotationAttribute {
    data class Color(val color: RinneAnnotationsColor) : RinneAnnotationAttribute
    data class FontWeight(val fontWeight: RinneAnnotationsFontWeight) : RinneAnnotationAttribute
    data class FontSize(val fontSize: TextUnit) : RinneAnnotationAttribute

    companion object
}

internal fun RinneAnnotationAttribute.Companion.parse(attribute: String, value: String) =
    when (attribute) {
        "color" -> RinneAnnotationsColor.find(value)
            ?.let(RinneAnnotationAttribute::Color)

        "fontWeight" -> RinneAnnotationsFontWeight.find(value)
            ?.let(RinneAnnotationAttribute::FontWeight)

        "fontSize" -> value.toIntOrNull()?.sp
            ?.let(RinneAnnotationAttribute::FontSize)

        else -> null
    }
