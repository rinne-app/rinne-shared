package com.rinne.libraries.text.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Immutable
sealed interface Text {
    data class Simple(val text: String) : Text
    data class AnnotatedText(val text: AnnotatedString) : Text
    data class Resource(val resource: StringResource, val params: List<Any> = emptyList()) : Text

    companion object {
        val Empty = Simple("")
    }
}

fun Text?.orEmpty() = this ?: Text.Empty

suspend fun Text.getString(): String = when (this) {
    is Text.Simple -> text
    is Text.AnnotatedText -> text.toString()
    is Text.Resource -> org.jetbrains.compose.resources.getString(resource)
}

val Text.displayText: String
    @Composable get() = when (this) {
        is Text.Simple -> text
        is Text.AnnotatedText -> text.toString()
        is Text.Resource -> when (params.isEmpty()) {
            true -> stringResource(resource)
            false -> stringResource(resource, *params.toTypedArray())
        }
    }
