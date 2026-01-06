package com.rinne.libraries.error.core.result

import com.rinne.libraries.error.core.RinneException


sealed interface RinneResultState<out T> {
    data object None : RinneResultState<Nothing>
    data object Loading : RinneResultState<Nothing>
    data class Error(val error: RinneException) : RinneResultState<Nothing>
    data class Success<T>(val data: T) : RinneResultState<T>
}