package com.rinne.libraries.network.client.offline

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

sealed interface RinneNetworkResult<out T> {
    /** `true` once the value reflects everything the request's cache policy intends to fetch. */
    val isFinal: Boolean

    data class Data<out T>(
        val value: T,
        val source: RinneDataSource,
        override val isFinal: Boolean,
        /** Unsent local mutations are applied on top of [value] (optimistic UI). */
        val hasPendingMutations: Boolean = false,
        /** Set when a refresh failed and [value] is the stored fallback. */
        val refreshError: Throwable? = null,
    ) : RinneNetworkResult<T>

    /** Nothing to show: no stored value and the network (if allowed) failed. */
    data class Error(val cause: Throwable) : RinneNetworkResult<Nothing> {
        override val isFinal: Boolean = true
    }
}

enum class RinneDataSource { Cache, Network }

/** The final value of the request, or the error that prevented it. */
suspend fun <T> Flow<RinneNetworkResult<T>>.await(): T = first { it.isFinal }.valueOrThrow()

/** Plain values; an [RinneNetworkResult.Error] is thrown into the collector. */
fun <T> Flow<RinneNetworkResult<T>>.values(): Flow<T> = map { it.valueOrThrow() }

fun <T> RinneNetworkResult<T>.valueOrThrow(): T = when (this) {
    is RinneNetworkResult.Data -> value
    is RinneNetworkResult.Error -> throw cause
}

fun <T> RinneNetworkResult<T>.valueOrNull(): T? = (this as? RinneNetworkResult.Data)?.value

/** [com.rinne.libraries.network.client.offline.cache.RinneCachePolicy.CacheOnly] found nothing stored. */
class RinneCacheMissException(path: String) : Exception("Nothing stored for $path")

/** The path references an entity that only exists locally until its create request is synced. */
class RinneEntityNotSyncedException(path: String) : Exception("$path references an entity that isn't synced yet")
