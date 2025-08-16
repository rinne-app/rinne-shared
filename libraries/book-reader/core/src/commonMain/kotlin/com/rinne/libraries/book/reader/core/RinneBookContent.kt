package com.rinne.libraries.book.reader.core

data class RinneBook(
    val content: List<RinneBookContent>,
)

sealed interface RinneBookContent {
    data class Title(val text: String) : RinneBookContent
    data class Text(val text: String) : RinneBookContent
}