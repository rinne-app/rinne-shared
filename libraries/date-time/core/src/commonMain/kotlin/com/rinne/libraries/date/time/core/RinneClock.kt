package com.rinne.libraries.date.time.core

import com.rinne.libraries.date.time.core.kotlinx.RinneTimestampKotlinx

/**
 * Source of the current [RinneTimestamp]. Inject it instead of reading the system clock directly,
 * so that time-dependent logic can be tested with a fixed or controllable clock.
 */
fun interface RinneClock {
    fun now(): RinneTimestamp

    companion object {
        /** The real system clock. */
        val System: RinneClock = RinneClock { RinneTimestamp(RinneTimestampKotlinx.nowEpochMillis()) }
    }
}
