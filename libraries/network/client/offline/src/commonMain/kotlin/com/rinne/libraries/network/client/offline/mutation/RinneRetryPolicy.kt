package com.rinne.libraries.network.client.offline.mutation

import com.rinne.libraries.date.time.core.RinneDuration
import com.rinne.libraries.date.time.core.inWholeMilliseconds
import com.rinne.libraries.date.time.core.minutes
import com.rinne.libraries.date.time.core.seconds
import kotlin.math.min
import kotlin.math.pow

data class RinneRetryPolicy(
    val initialDelay: RinneDuration = 2.seconds,
    val maxDelay: RinneDuration = 5.minutes,
    val multiplier: Double = 2.0,
    /** After this many failed attempts a mutation is marked failed instead of retried. */
    val maxAttempts: Int = 20,
) {
    internal fun delayMillis(attempt: Int): Long {
        val exponential = initialDelay.inWholeMilliseconds * multiplier.pow(attempt - 1)
        return min(exponential, maxDelay.inWholeMilliseconds.toDouble()).toLong()
    }

    internal companion object {
        /** Statuses where the same request may succeed later. 425 = the server is still processing the same key. */
        fun isRetryableStatus(status: Int) = status == 408 || status == 425 || status == 429 || status >= 500

        fun isConflictStatus(status: Int) = status == 409 || status == 412
    }
}
