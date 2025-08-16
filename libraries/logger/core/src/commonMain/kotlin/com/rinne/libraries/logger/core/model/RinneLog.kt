package com.rinne.libraries.logger.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class RinneLog(
    val tag: String,
    val message: String,
    val type: RinneLogType,
    val dateTime: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
)