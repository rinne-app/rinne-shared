package com.rinne.libraries.network.client.ktor

// The Darwin engine reports connectivity failures as IOException subclasses.
internal actual fun Throwable.isPlatformConnectivityFailure(): Boolean = false
