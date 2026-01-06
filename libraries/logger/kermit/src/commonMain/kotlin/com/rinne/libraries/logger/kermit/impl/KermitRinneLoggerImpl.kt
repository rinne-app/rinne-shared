package com.rinne.libraries.logger.kermit.impl

import co.touchlab.kermit.Logger
import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.model.RinneLog
import com.rinne.libraries.logger.core.model.RinneLogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.rinne.libraries.date.time.core.RinneDateTime

internal class KermitRinneLoggerImpl(override val defaultTag: String) : RinneLogger {
    private val _logsStateFlow =
        MutableStateFlow<List<RinneLog>>(emptyList())
    override val logsStateFlow = _logsStateFlow.asStateFlow()

    override fun log(log: RinneLog) {
        printLog(log)
        _logsStateFlow.update { it + log }
    }

    override fun log(
        message: String,
        type: RinneLogType,
        tag: String,
        dateTime: RinneDateTime
    ) {
        log(log = RinneLog(tag = tag, message = message, type = type, dateTime = dateTime))
    }

    override fun clearLogs() {
        _logsStateFlow.update { emptyList() }
    }

    private fun printLog(log: RinneLog) {
        when (val type = log.type) {
            RinneLogType.Debug -> Logger.Companion.d(log.tag) { log.message }
            is RinneLogType.Error -> Logger.Companion.e(log.tag, type.throwable) { log.message }
            RinneLogType.Info -> Logger.Companion.i(log.tag) { log.message }
            RinneLogType.Warning -> Logger.Companion.w(log.tag) { log.message }
        }
    }
}