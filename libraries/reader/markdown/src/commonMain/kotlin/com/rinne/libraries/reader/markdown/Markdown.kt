package com.rinne.libraries.reader.markdown

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rinne.libraries.reader.markdown.deprecated.Markdown

@Composable
fun RinneMarkdown(
    content: String,
    modifier: Modifier = Modifier
) {
    Markdown(
        content = content,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    )
}