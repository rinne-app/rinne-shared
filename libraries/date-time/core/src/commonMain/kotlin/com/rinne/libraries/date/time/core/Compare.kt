package com.rinne.libraries.date.time.core

fun RinneInstant.isBefore(other: RinneInstant): Boolean =
    this.epochMillis < other.epochMillis

fun RinneInstant.isAfter(other: RinneInstant): Boolean =
    this.epochMillis > other.epochMillis

fun RinneInstant.durationBetween(other: RinneInstant): RinneDuration =
    (other.epochMillis - this.epochMillis).milliseconds
