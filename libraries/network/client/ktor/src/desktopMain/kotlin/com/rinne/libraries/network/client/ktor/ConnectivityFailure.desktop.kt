package com.rinne.libraries.network.client.ktor

import java.nio.channels.UnresolvedAddressException

// CIO reports an unknown host with UnresolvedAddressException, which is not an IOException.
internal actual fun Throwable.isPlatformConnectivityFailure(): Boolean = this is UnresolvedAddressException
