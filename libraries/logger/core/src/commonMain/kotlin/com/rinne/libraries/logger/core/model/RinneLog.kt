package com.rinne.libraries.logger.core.model

import com.rinne.libraries.date.time.core.RinneDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class RinneLog(
    val tag: String,
    val message: String,
    val type: RinneLogType,
    val dateTime: RinneDateTime = RinneDateTime.now(),
)