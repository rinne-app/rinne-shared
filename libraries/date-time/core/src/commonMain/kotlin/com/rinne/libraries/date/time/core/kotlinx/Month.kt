package com.rinne.libraries.date.time.core.kotlinx

import com.rinne.libraries.date.time.core.RinneMonth
import kotlinx.datetime.Month

internal fun Month.asRinneMonth() = when (this) {
    Month.JANUARY -> RinneMonth.JANUARY
    Month.FEBRUARY -> RinneMonth.FEBRUARY
    Month.MARCH -> RinneMonth.MARCH
    Month.APRIL -> RinneMonth.APRIL
    Month.MAY -> RinneMonth.MAY
    Month.JUNE -> RinneMonth.JUNE
    Month.JULY -> RinneMonth.JULY
    Month.AUGUST -> RinneMonth.AUGUST
    Month.SEPTEMBER -> RinneMonth.SEPTEMBER
    Month.OCTOBER -> RinneMonth.OCTOBER
    Month.NOVEMBER -> RinneMonth.NOVEMBER
    Month.DECEMBER -> RinneMonth.DECEMBER
}