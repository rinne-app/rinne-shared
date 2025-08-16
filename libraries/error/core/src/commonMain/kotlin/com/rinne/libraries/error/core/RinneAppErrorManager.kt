package com.rinne.libraries.error.core

import kotlinx.coroutines.flow.StateFlow

interface RinneAppErrorManager {
    val appErrorStateFlow: StateFlow<RinneException?>
    val errorsHistoryStateFlow: StateFlow<List<RinneException>>

    fun reduceError(throwable: Throwable): RinneException?

    fun clearAppError(exception: RinneException)
}