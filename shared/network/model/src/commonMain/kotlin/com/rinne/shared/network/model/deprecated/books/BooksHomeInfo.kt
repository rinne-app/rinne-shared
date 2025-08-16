package com.rinne.shared.network.model.deprecated.books

import com.rinne.shared.network.model.deprecated.ProfileInfo

data class BooksHomeInfo(
    val books: List<BookInfo>,
    val userInfo: ProfileInfo,
)

data class BookInfo(
    val id: String,
    val title: String,
    val author: String,
    val cover: String?,
)

data class BookDetails(
    val id: String,
    val title: String,
    val author: String,
    val cover: String?,
    val text: String,
)

fun BookDetails.asInfo() = BookInfo(
    id = id,
    title = title,
    author = author,
    cover = cover,
)