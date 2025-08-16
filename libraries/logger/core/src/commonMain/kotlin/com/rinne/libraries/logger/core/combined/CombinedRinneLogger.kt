package com.rinne.libraries.logger.core.combined

import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.model.RinneLog
import com.rinne.libraries.logger.core.model.RinneLogType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDateTime

internal class CombinedRinneLogger(
    loggers: List<RinneLogger>,
    coroutineScope: CoroutineScope,
    private val mainLogger: RinneLogger,
) : RinneLogger {
    private val loggers = (loggers + mainLogger).toSet()

    override val logsStateFlow = combine((loggers).map { it.logsStateFlow }) {
        it.flatMap { it }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), emptyList())

    override val defaultTag: String get() = mainLogger.defaultTag

    override fun log(log: RinneLog) {
        mainLogger.log(log)
    }

    override fun log(
        message: String,
        type: RinneLogType,
        tag: String,
        dateTime: LocalDateTime
    ) {
        mainLogger.log(message, type, tag, dateTime)
    }

    override fun clearLogs() {
        loggers.forEach { it.clearLogs() }
    }
}