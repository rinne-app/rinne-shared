package com.rinne.libraries.error.compose.result

import com.rinne.libraries.error.core.RinneException

sealed interface RinneResultUi<out T> {
    sealed interface WithData<T> : RinneResultUi<T> {
        val data: T
    }


    data object Loading : RinneResultUi<Nothing>

    /*
    title
    message
    button with actions (back, retry, log out) - ?


    should be enum? like NO_INTERNET_CONNECTION, UNAUTHORIZED, etc?

    should be navigation through AppRouteManager to UNAUTHORIZED state for example
    also write to help support
     */
    data class Error(
        val error: RinneException,
    ) : RinneResultUi<Nothing>

    data class Success<T>(override val data: T) : WithData<T>
    data class Updating<T>(override val data: T) : WithData<T>
}

val <T> RinneResultUi<T>.isUpdating
    get() = this is RinneResultUi.Updating

fun <T> RinneResultUi<T>.getDataOrNull() = when (this is RinneResultUi.WithData<T>) {
    true -> data
    false -> null
}