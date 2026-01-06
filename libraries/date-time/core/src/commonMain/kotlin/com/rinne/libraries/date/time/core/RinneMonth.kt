package com.rinne.libraries.date.time.core

enum class RinneMonth {
    /** January, month #01, with 31 days. */
    JANUARY,

    /** February, month #02, with 28 days, or 29 in leap years. */
    FEBRUARY,

    /** March, month #03, with 31 days. */
    MARCH,

    /** April, month #04, with 30 days. */
    APRIL,

    /** May, month #05, with 31 days. */
    MAY,

    /** June, month #06, with 30 days. */
    JUNE,

    /** July, month #07, with 31 days. */
    JULY,

    /** August, month #08, with 31 days. */
    AUGUST,

    /** September, month #09, with 30 days. */
    SEPTEMBER,

    /** October, month #10, with 31 days. */
    OCTOBER,

    /** November, month #11, with 30 days. */
    NOVEMBER,

    /** December, month #12, with 31 days. */
    DECEMBER;

    companion object {
        val first = JANUARY
    }
}

fun RinneMonth.Companion.of(number: Int): RinneMonth {
    return RinneMonth.entries.first { it.number == number }
}


val RinneMonth.number
    get() = ordinal + 1