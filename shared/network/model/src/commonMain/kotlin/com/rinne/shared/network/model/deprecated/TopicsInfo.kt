package com.rinne.shared.network.model.deprecated

data class TopicsInfo(
    val sections: List<TopicSection>,
)

sealed interface TopicSection {
    data class Pages(val items: List<PageShortInfo>) : TopicSection
    data class Communities(val items: List<CommunityInfo>) : TopicSection
}