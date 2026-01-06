package com.rinne.libraries.date.time.core

import com.rinne.libraries.date.time.core.RinneDuration.Companion.ZERO
import com.rinne.libraries.date.time.core.RinneMath.floorMod
import kotlin.math.roundToLong
import kotlin.time.Duration

internal const val MAX_SECONDS = Long.MAX_VALUE / 2

data class RinneDuration internal constructor(
    val seconds: Long,
    val nanoseconds: Int
) : Comparable<RinneDuration> {

    init {
        require(seconds in -MAX_SECONDS..MAX_SECONDS) {
            "$seconds s is out of range"
        }
        require(nanoseconds in -999_999_999..999_999_999) {
            "$nanoseconds ns is out of nanoseconds range"
        }
    }

    override fun compareTo(other: RinneDuration): Int {
        val secCmp = seconds.compareTo(other.seconds)
        return if (secCmp != 0) secCmp else nanoseconds.compareTo(other.nanoseconds)
    }

    companion object {
        val ZERO = RinneDuration(0, 0)
        val INFINITE = RinneDuration(MAX_SECONDS, 999_999_999)
        val NEG_INFINITE = RinneDuration(-MAX_SECONDS, -999_999_999)

        fun of(totalSeconds: Long, nanoAdjustment: Long): RinneDuration {
            val sec = totalSeconds + nanoAdjustment.floorDiv(1_000_000_000)
            val nanos = floorMod(nanoAdjustment, 1_000_000_000).toInt()
            return RinneDuration(sec, nanos).normalize()
        }
    }
}


/** Normalizes signs and overflows */
private fun RinneDuration.normalize(): RinneDuration {
    if (seconds == 0L) {
        // nanoseconds may be negative or positive, that's fine
        return this
    }
    if (nanoseconds == 0) return this

    // ensure signs are consistent:
    return if (seconds > 0 && nanoseconds < 0) {
        RinneDuration(seconds - 1, nanoseconds + 1_000_000_000)
    } else if (seconds < 0 && nanoseconds > 0) {
        RinneDuration(seconds + 1, nanoseconds - 1_000_000_000)
    } else this
}


operator fun RinneDuration.times(factor: Double): RinneDuration {
    require(!factor.isNaN()) { "Cannot multiply duration by NaN" }

    val totalNanos = seconds * 1_000_000_000L + nanoseconds
    val scaledNanos = (totalNanos * factor).toLong() // rounding down
    val newSeconds = scaledNanos / 1_000_000_000
    val newNanos = (scaledNanos % 1_000_000_000).toInt()
    return RinneDuration(newSeconds, newNanos)
}

val RinneDuration.inWholeNanoseconds: Long
    get() = seconds * 1_000_000_000L + nanoseconds

val RinneDuration.inWholeMicroseconds: Long
    get() = seconds * 1_000_000L + nanoseconds / 1_000

val RinneDuration.inWholeMilliseconds: Long
    get() = seconds * 1_000L + nanoseconds / 1_000_000

val RinneDuration.inWholeSeconds: Long
    get() = seconds + nanoseconds / 1_000_000_000

val RinneDuration.inWholeMinutes: Long
    get() = inWholeSeconds / 60

val RinneDuration.inWholeHours: Long
    get() = inWholeMinutes / 60

val RinneDuration.inWholeDays: Long
    get() = inWholeHours / 24

// --- Long-based ---
val Long.nanoseconds: RinneDuration
    get() = RinneDuration.of(0, this)

val Long.microseconds: RinneDuration
    get() = RinneDuration.of(0, this * 1_000)

val Long.milliseconds: RinneDuration
    get() = RinneDuration.of(this / 1000, (this % 1000) * 1_000_000)

val Long.seconds: RinneDuration
    get() = RinneDuration(this, 0)

val Long.minutes: RinneDuration
    get() = RinneDuration(this * 60, 0)

val Long.hours: RinneDuration
    get() = RinneDuration(this * 3600, 0)

val Long.days: RinneDuration
    get() = RinneDuration(this * 86400, 0)

// --- Int-based ---
val Int.nanoseconds: RinneDuration
    get() = RinneDuration.of(0, this.toLong())

val Int.microseconds: RinneDuration
    get() = RinneDuration.of(0, this * 1_000L)

val Int.milliseconds: RinneDuration
    get() = RinneDuration.of(this / 1000L, (this % 1000L) * 1_000_000)

val Int.seconds: RinneDuration
    get() = RinneDuration(this.toLong(), 0)

val Int.minutes: RinneDuration
    get() = RinneDuration(this * 60L, 0)

val Int.hours: RinneDuration
    get() = RinneDuration(this * 3600L, 0)

val Int.days: RinneDuration
    get() = RinneDuration(this * 86400L, 0)

// --- Double-based (rounded safely) ---
private fun Double.requireNotNaN() = also {
    require(!isNaN()) { "Duration cannot be constructed from NaN." }
}

val Double.seconds: RinneDuration
    get() {
        requireNotNaN()
        val sec = toLong()
        val nanos = ((this - sec) * 1_000_000_000).roundToLong()
        return RinneDuration.of(sec, nanos)
    }

val Double.milliseconds: RinneDuration
    get() = (this.requireNotNaN() * 1_000_000).roundToLong().nanoseconds

val Double.microseconds: RinneDuration
    get() = (this.requireNotNaN() * 1_000).roundToLong().nanoseconds

val Double.nanoseconds: RinneDuration
    get() = this.requireNotNaN().roundToLong().nanoseconds

val Double.minutes: RinneDuration
    get() = (this.requireNotNaN() * 60).seconds

val Double.hours: RinneDuration
    get() = (this.requireNotNaN() * 3600).seconds

val Double.days: RinneDuration
    get() = (this.requireNotNaN() * 86400).seconds


object RinneMath {
    /** (mathematical division) */
    fun floorDiv(a: Long, b: Long): Long {
        val div = a / b
        val rem = a % b
        return if (rem == 0L || (a xor b) >= 0) div else div - 1
    }

    /** (mathematical division) */
    fun floorDiv(a: Int, b: Int): Int {
        val div = a / b
        val rem = a % b
        return if (rem == 0 || (a xor b) >= 0) div else div - 1
    }

    /** always returns positive remainder for positive divisor */
    fun floorMod(a: Long, b: Long): Long =
        a - floorDiv(a, b) * b

    /** always returns positive remainder for positive divisor */
    fun floorMod(a: Int, b: Int): Int =
        a - floorDiv(a, b) * b
}