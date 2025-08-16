package com.rinne.libraries.error.core

/*
TODO replace maybe to libraries:error:ktor-client
 */
sealed class RinneKtorClient : RinneException() {

    data class Unauthorized(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : RinneKtorClient()

    data class Unknown(
        val statusCode: String,
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : RinneKtorClient()
}