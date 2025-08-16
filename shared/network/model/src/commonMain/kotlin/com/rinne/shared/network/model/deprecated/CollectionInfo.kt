package com.rinne.shared.network.model.deprecated

data class CollectionInfo(
    val id: String,
    val title: String,
    val tag: String,
    val items: List<CollectionItem>,
    val filters: List<CollectionFilter>,
)

data class CollectionFilter(
    val id: String,
    val title: String,
    val applied: Boolean,
)

data class CollectionItem(
    val id: String,
    val cover: String,
    val title: String,
    val subtitle: String,
)