package com.rinne.shared.network.model.deprecated

data class PageShortInfo(
    val id: String,
    val tag: String,
    val type: PageType,
    val cover: String,
    val title: String,
)