package com.rinne.libraries.network.client.offline.cache

/** Outcome of [com.rinne.libraries.network.client.offline.RinneClient.refreshCache]. */
data class RinneCacheRefreshReport(
    /** Requests fetched again (or for the first time, by a prefetch). */
    val refreshed: Int,
    /** Stored responses dropped because the server no longer has them (404/410). */
    val removed: Int,
    /** Requests that failed; their stored responses are kept. */
    val failed: Int,
)
