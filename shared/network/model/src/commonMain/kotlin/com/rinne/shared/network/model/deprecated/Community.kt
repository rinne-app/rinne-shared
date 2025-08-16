package com.rinne.shared.network.model.deprecated

data class CommunityDetails(
    val id: String,
    val name: String,
    val displayId: String, //TODO rename
    val image: String,
    val rating: String,
    val articles: List<ArticleInfo>,
    val tags: List<CommunityTagInfo>,
    val extensions: List<CommunityExtension>,
)

sealed interface CommunityExtension {
    data class RssChannel(val channel: String) : CommunityExtension
}

fun CommunityDetails.asInfo() = CommunityInfo(
    id = id,
    name = name,
    image = image,
    rating = rating,
    tags = tags,
    displayId = displayId,
)

data class CommunityInfo(
    val id: String,
    val name: String,
    val displayId: String, //TODO rename
    val image: String,
    val rating: String,
    val tags: List<CommunityTagInfo>,
)

data class CommunityTagInfo(
    val id: String,
    val name: String,
)