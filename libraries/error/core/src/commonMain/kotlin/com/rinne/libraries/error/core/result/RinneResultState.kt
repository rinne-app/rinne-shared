package com.rinne.libraries.error.core.result


sealed interface RinneResultState<out T> {
    data object Loading : RinneResultState<Nothing>
    data class Error(val error: Throwable) : RinneResultState<Nothing>
    data class Success<T>(val data: T) : RinneResultState<T>
}
