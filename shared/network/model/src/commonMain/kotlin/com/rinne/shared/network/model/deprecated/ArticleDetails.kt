package com.rinne.shared.network.model.deprecated

import com.rinne.libraries.date.time.core.RinneDateTime

//TODO co-authors
data class ArticleDetails(
    val id: String,
    val title: String,
    val text: String,
    val image: String?,
    val rating: String,
    val bookmarked: Boolean,
    val commentsCount: String,
    val mainTag: String,
    val author: ProfileInfo,
    val dateTime: RinneDateTime,
    val discussion: Discussion,
    val tags: List<String>,
)

fun ArticleDetails.asInfo() = ArticleInfo(
    id = id,
    image = image,
    rating = rating,
    author = author,
    commentsCount = commentsCount,
    title = title,
    text = text,
    tags = tags,
    dateTime = dateTime
)