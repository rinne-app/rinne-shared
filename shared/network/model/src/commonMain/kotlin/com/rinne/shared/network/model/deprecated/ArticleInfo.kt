package com.rinne.shared.network.model.deprecated

import kotlinx.datetime.LocalDateTime

data class ArticlesInfo(
    val tag: String,
    val filters: List<String>,
    val items: List<ArticleInfo>,
)

data class ArticleInfo(
    val id: String,
    val image: String?,
    val rating: String,
    val author: ProfileInfo,
    val commentsCount: String,
    val title: String,
    val text: String,
    val tags: List<String>,
    val dateTime: LocalDateTime,
)
