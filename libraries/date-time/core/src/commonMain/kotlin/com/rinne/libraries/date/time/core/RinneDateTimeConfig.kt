package com.rinne.libraries.date.time.core

import com.rinne.libraries.date.time.core.kotlinx.RinneDateProviderKotlinx
import com.rinne.libraries.date.time.core.kotlinx.RinneDateTimeProviderKotlinx
import com.rinne.libraries.date.time.core.kotlinx.RinneTimeProviderKotlinx

object RinneDateTimeConfig {
    internal val timeProvider: RinneTimeProvider = RinneTimeProviderKotlinx
    internal  val dateProvider: RinneDateProvider = RinneDateProviderKotlinx
    internal val dateTimeProvider: RinneDateTimeProvider = RinneDateTimeProviderKotlinx
}