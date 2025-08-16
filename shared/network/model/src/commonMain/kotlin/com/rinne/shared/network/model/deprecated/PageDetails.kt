package com.rinne.shared.network.model.deprecated

import kotlinx.datetime.LocalDateTime

data class PageDetails(
    val id: String,
    val tag: String,
    val type: PageType,
    val cover: String,
    val title: String,
    val text: String,
    val relationshipTypes: List<RelationshipType>,
    val filters: List<PageFilters>,
    val sectionsShortInfo: List<PageSectionShortInfo>,
)

//TODO gallery, related/similar pages
data class PageSectionShortInfo(
    val id: String,
    val title: String,
    val text: String,
)

fun PageDetails.asShortInfo() = PageShortInfo(
    id = id,
    tag = tag,
    type = type,
    cover = cover,
    title = title,
)

data class RelationshipType(
    val id: String,
    val label: String,
)

sealed interface PageFilters {
    data class DateTime(val data: LocalDateTime) : PageFilters
    data class Location(val latLng: RinneLatLng) : PageFilters
}


//add graphics, charts, table =
sealed interface PageSections {
    data class Graph(val title: String) : PageSections
    data class Text(val text: String, val title: String) : PageSections
}

enum class PageType {
    DEFAULT, PLACE, PERSON
}