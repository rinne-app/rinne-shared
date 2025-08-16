package com.rinne.libraries.text.highlights

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.extensions.i
import com.rinne.libraries.text.highlights.impl.applyAnnotation
import com.rinne.libraries.text.highlights.impl.parser.AnnotatedStringParser
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun annotatedStringResource(
    resource: StringResource,
    vararg formatArgs: Any,
    colors: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography,
): AnnotatedString {
    val string = stringResource(resource, *formatArgs)
    val annotatedString by derivedStateOf { AnnotatedStringParser.parse(string.trimIndent()) }

    return remember(string, *formatArgs, colors, typography) {
        buildAnnotatedString {
            append(annotatedString.plainText)

            RinneLogger.i("string: $string")
            RinneLogger.i("annotations: ${annotatedString.annotations}")

            annotatedString.annotations.forEach {
                applyAnnotation(
                    annotation = it,
                    theme = TextHighlightsTheme(colors, typography)
                )
            }
        }
    }
}