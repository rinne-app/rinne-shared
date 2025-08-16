package com.rinne.libraries.text.highlights.impl.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.rinne.libraries.text.core.Text
import com.rinne.libraries.text.highlights.annotatedStringResource

val Text.annotatedString: AnnotatedString
    @Composable get() = when (this) {
        is Text.AnnotatedText -> this.text
        is Text.Resource -> annotatedStringResource(resource, *params.toTypedArray())
        is Text.Simple -> buildAnnotatedString { append(text) }
    }
