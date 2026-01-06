package com.rinne.shared.network.model.deprecated

import com.rinne.libraries.date.time.core.RinneDate

data class TimelineDetails(
    val pageInfo: PageShortInfo,
    val years: List<TimelineYear>,
)

data class TimelineYear(
    val info: TimelineYearInfo,
    val events: List<TimelineYearEvent>,
)

data class TimelineYearInfo(
    val id: String,
    val year: String,
)

data class TimelineYearEvent(
    val id: String,
    val cover: String?,
    val title: String,
    val description: String,
    val date: RinneDate,
)