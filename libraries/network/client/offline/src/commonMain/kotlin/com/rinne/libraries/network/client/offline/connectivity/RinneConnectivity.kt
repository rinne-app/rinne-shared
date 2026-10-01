package com.rinne.libraries.network.client.offline.connectivity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Platform network availability; the outbox replays queued mutations when it turns online. */
interface RinneConnectivity {
    val isOnline: StateFlow<Boolean>

    companion object {
        /** For platforms without a monitor: failures are then detected per request and retried with backoff. */
        val AlwaysOnline: RinneConnectivity = object : RinneConnectivity {
            override val isOnline: StateFlow<Boolean> = MutableStateFlow(true).asStateFlow()
        }
    }
}
