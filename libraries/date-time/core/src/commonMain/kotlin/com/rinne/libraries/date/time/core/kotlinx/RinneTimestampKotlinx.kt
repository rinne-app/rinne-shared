package com.rinne.libraries.date.time.core.kotlinx

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal object RinneTimestampKotlinx {

    fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()

    fun parse(input: String): Long = Instant.parse(input).toEpochMilliseconds()

    fun format(epochMillis: Long): String = Instant.fromEpochMilliseconds(epochMillis).toString()
}
