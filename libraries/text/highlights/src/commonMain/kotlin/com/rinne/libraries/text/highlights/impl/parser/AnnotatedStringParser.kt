package com.rinne.libraries.text.highlights.impl.parser

import com.rinne.libraries.text.highlights.impl.model.RinneAnnotatedString
import com.rinne.libraries.text.highlights.impl.model.RinneAnnotation
import com.rinne.libraries.text.highlights.impl.model.RinneAnnotationAttribute
import com.rinne.libraries.text.highlights.impl.model.parse

internal object AnnotatedStringParser {

    fun parse(source: String): RinneAnnotatedString {
        val annotationRegex = Regex("""(<annotation\s+([^>]+)>)(.*?)(</annotation>)""")
        val attributeRegex = Regex("""(\w+)="([^"]*)"""")

        val annotations = mutableListOf<RinneAnnotation>()
        val plainText = StringBuilder(source)

        var removedCount = 0

        annotationRegex.findAll(source).forEach { match ->
            val attributes = mutableMapOf<String, String>()

            // Extract attributes
            val attributeText = match.groups[1]?.value.orEmpty()
            attributeRegex.findAll(attributeText).forEach { attrMatch ->
                val key = attrMatch.groups[1]?.value.orEmpty()
                val value = attrMatch.groups[2]?.value.orEmpty()
                attributes[key] = value
            }

            // Extract annotation content
            val content = match.groups[2]?.value.orEmpty()

            val tagLength = match.groups[1]?.value?.length
            val start = match.range.first - removedCount
            if (tagLength != null) plainText.deleteRange(
                match.range.first - removedCount,
                match.range.first + tagLength - removedCount,
            ).also {
                removedCount += tagLength
            }

            val closedTagLength = match.groups[4]?.value?.length
            val end = match.range.last + 1 - removedCount - (closedTagLength ?: 0)
            if (closedTagLength != null && tagLength != null) plainText.deleteRange(
                match.range.last + 1 - removedCount - closedTagLength,
                match.range.last + 1 - removedCount,
            ).also {
                removedCount += closedTagLength
            }

            // Create AnnotationSpan
            attributes
                .mapNotNull {
                    RinneAnnotationAttribute.parse(attribute = it.key, value = it.value)
                }
                .map {
                    RinneAnnotation(
                        value = content,
                        start = start,
                        end = end,
                        attribute = it
                    )
                }
                .also(annotations::addAll)
        }

        return RinneAnnotatedString(
            plainText = plainText.toString(),
            annotations = annotations,
        )
    }
}