package com.rinne.libraries.rss.reader.core.model

data class RinneRssChannel(
    val title: String?,
    val link: String?,
    val description: String?,
    val image: RinneRssImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    val items: List<RinneRssItem>,
//    val itunesChannelData: ItunesChannelData?,
//    val youtubeChannelData: YoutubeChannelData?,
)

data class RinneRssImage(
    val title: String?,
    val url: String?,
    val link: String?,
    val description: String?
) {
    internal fun isNotEmpty(): Boolean {
        return !url.isNullOrBlank() || !link.isNullOrBlank()
    }
}

data class RinneRssItem(
    val guid: String?,
    val title: String?,
    val author: String?,
    val link: String?,
    val pubDate: String?,
    val description: String?,
    val content: String?,
    val image: String?,
    val audio: String?,
    val video: String?,
    val sourceName: String?,
    val sourceUrl: String?,
    val categories: List<String>,
//    val itunesItemData: ItunesItemData?,
    val commentsUrl: String?,
//    val youtubeItemData: YoutubeItemData?,
    val rawEnclosure: RinneRawEnclosure?,
)

//data class YoutubeChannelData(
//    val channelId: String?,
//) {
//    internal data class Builder(
//        var channelId: String? = null,
//    ) {
//        fun build() = YoutubeChannelData(
//            channelId = channelId,
//        )
//    }
//}
//
//data class YoutubeItemData(
//    val videoId: String?,
//    val title: String?,
//    val videoUrl: String?,
//    val thumbnailUrl: String?,
//    val description: String?,
//    val viewsCount: Int?,
//    val likesCount: Int?,
//)
//
//data class ItunesChannelData(
//    val author: String?,
//    val categories: List<String> = emptyList(),
//    val duration: String?,
//    val explicit: String?,
//    val image: String?,
//    val keywords: List<String>,
//    val newsFeedUrl: String?,
//    val owner: ItunesOwner?,
//    val subtitle: String?,
//    val summary: String?,
//    val type: String?,
//) {
//    internal data class Builder(
//        var author: String? = null,
//        var categories: MutableList<String> = mutableListOf(),
//        var duration: String? = null,
//        var explicit: String? = null,
//        var image: String? = null,
//        var keywords: List<String> = emptyList(),
//        var newsFeedUrl: String? = null,
//        var owner: ItunesOwner? = null,
//        var subtitle: String? = null,
//        var summary: String? = null,
//        var type: String? = null,
//    ) {
//        fun build() = ItunesChannelData(
//            author = author,
//            categories = categories,
//            duration = duration,
//            explicit = explicit,
//            image = image,
//            keywords = keywords,
//            newsFeedUrl = newsFeedUrl,
//            owner = owner,
//            subtitle = subtitle,
//            summary = summary,
//            type = type,
//        )
//    }
//}
//
//data class ItunesOwner(
//    val name: String?,
//    val email: String?,
//) {
//    internal data class Builder(
//        var name: String? = null,
//        var email: String? = null,
//    ) {
//        fun build() = ItunesOwner(name, email)
//    }
//}
//
//data class ItunesItemData(
//    val author: String?,
//    val duration: String?,
//    val episode: String?,
//    val episodeType: String?,
//    val explicit: String?,
//    val image: String?,
//    val keywords: List<String>,
//    val subtitle: String?,
//    val summary: String?,
//    val season: String?,
//) {
//    internal data class Builder(
//        var author: String? = null,
//        var duration: String? = null,
//        var episode: String? = null,
//        var episodeType: String? = null,
//        var explicit: String? = null,
//        var image: String? = null,
//        var keywords: List<String> = emptyList(),
//        var subtitle: String? = null,
//        var summary: String? = null,
//        var season: String? = null,
//    ) {
//        fun build() = ItunesItemData(
//            author = author,
//            duration = duration,
//            episode = episode,
//            episodeType = episodeType,
//            explicit = explicit,
//            image = image,
//            keywords = keywords,
//            subtitle = subtitle,
//            summary = summary,
//            season = season,
//        )
//    }
//}

data class RinneRawEnclosure(
    val url: String?,
    val length: Long?,
    val type: String?,
) {
    internal data class Builder(
        var url: String? = null,
        var length: Long? = null,
        var type: String? = null,
    ) {
        fun build() = RinneRawEnclosure(
            url = url,
            length = length,
            type = type
        )
    }
}