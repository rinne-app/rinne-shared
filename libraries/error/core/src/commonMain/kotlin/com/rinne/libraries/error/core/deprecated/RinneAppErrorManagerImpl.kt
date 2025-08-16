package com.rinne.libraries.error.core.deprecated

import com.rinne.libraries.error.core.RinneAppErrorManager
import com.rinne.libraries.error.core.RinneException
import com.rinne.libraries.error.core.RinneIsAppLevelErrorProvider
import com.rinne.libraries.error.core.asRinneException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/*
should do it on ui level maybe

interceptor or Error manager that will handle some errors. If it handle error - skip this error for lower levels, if not - send it to lower level

error vs exception
 */
internal class RinneAppErrorManagerImpl(
    private val isAppLevelErrorProvider: RinneIsAppLevelErrorProvider,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob()),
) : RinneAppErrorManager {
    override val appErrorStateFlow = MutableStateFlow<RinneException?>(null)
    private val appErrorMutex = Mutex()

    override val errorsHistoryStateFlow = MutableStateFlow<List<RinneException>>(emptyList())

    override fun reduceError(throwable: Throwable): RinneException? {
        val rinneException = throwable.asRinneException()

        errorsHistoryStateFlow.update { it + rinneException }

        if (isAppLevelErrorProvider.isAppLevelError(rinneException)) {
            emitAppError(rinneException)

            return null
        }

        return rinneException
    }

    override fun clearAppError(exception: RinneException) {
        emitAppError(exception)
    }

    private fun emitAppError(exception: RinneException?) {
        coroutineScope.launch {
            appErrorMutex.withLock { appErrorStateFlow.emit(exception) }
        }
    }
}
