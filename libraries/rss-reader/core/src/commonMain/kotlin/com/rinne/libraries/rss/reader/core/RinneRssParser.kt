package com.rinne.libraries.rss.reader.core

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RawEnclosure
import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssImage
import com.prof18.rssparser.model.RssItem
import com.rinne.libraries.rss.reader.core.model.RinneRawEnclosure
import com.rinne.libraries.rss.reader.core.model.RinneRssChannel
import com.rinne.libraries.rss.reader.core.model.RinneRssImage
import com.rinne.libraries.rss.reader.core.model.RinneRssItem

interface RinneRssParser {
    suspend fun parse(raw: String): RinneRssChannel

    companion object {
        @Suppress("FunctionName")
        fun Default(): RinneRssParser = RinneRssParserImpl()
    }
}

internal class RinneRssParserImpl() : RinneRssParser {
    override suspend fun parse(raw: String): RinneRssChannel {
        return RssParser().parse(raw).asRinne()
    }
}


internal fun RssChannel.asRinne(): RinneRssChannel {
    return RinneRssChannel(
        title = title,
        link = link,
        description = description,
        image = image?.asRinne(),
        lastBuildDate = lastBuildDate,
        updatePeriod = updatePeriod,
        items = items.map { it.asRinne() },
    )
}

internal fun RssImage.asRinne() = RinneRssImage(
    title = title,
    url = url,
    link = link,
    description = description,
)

internal fun RssItem.asRinne() = RinneRssItem(
    guid = guid,
    title = title,
    author = author,
    link = link,
    pubDate = pubDate,
    description = description,
    content = content,
    image = image,
    audio = audio,
    video = video,
    sourceName = sourceName,
    sourceUrl = sourceUrl,
    categories = categories,
    commentsUrl = commentsUrl,
    rawEnclosure = rawEnclosure?.asRinne(),
)

internal fun RawEnclosure.asRinne() = RinneRawEnclosure(
    url = url,
    length = length,
    type = type
)