package com.rinne.libraries.network.client.offline.cache

import com.rinne.libraries.date.time.core.RinneDuration

enum class RinneCachePolicy {
    /** Only the network; a failure is an error even if something is stored. */
    NetworkOnly,

    /** Only the local store; never hits the network. */
    CacheOnly,

    /** The stored value while it is fresh (see `maxAge`), otherwise the network with the stored value as fallback. */
    CacheFirst,

    /** The stored value right away (not final), then the network value (final). Stale-while-revalidate. */
    CacheAndNetwork,

    /** The network value; the stored value only if the network fails. */
    NetworkFirst,
}

internal data class RinneCacheOptions(
    val policy: RinneCachePolicy,
    val maxAge: RinneDuration?,
    val persist: Boolean,
    val observe: Boolean,
    val tags: Set<String>,
)
