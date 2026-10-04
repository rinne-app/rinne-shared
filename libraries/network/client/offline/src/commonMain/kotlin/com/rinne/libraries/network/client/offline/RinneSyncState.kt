package com.rinne.libraries.network.client.offline

data class RinneSyncState(
    /** Mutations waiting in the outbox. */
    val pendingCount: Int,
    /** Mutations the server rejected; they need a retry or discard. */
    val failedCount: Int,
    val isSyncing: Boolean,
    /** Why the outbox last stopped or rejected something; `null` after a successful send. */
    val lastError: Throwable?,
) {
    val isSynced: Boolean get() = pendingCount == 0 && failedCount == 0

    companion object {
        val Idle = RinneSyncState(pendingCount = 0, failedCount = 0, isSyncing = false, lastError = null)
    }
}
