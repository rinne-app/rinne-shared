package com.rinne.libraries.logger.core.impl

import com.rinne.libraries.date.time.core.RinneDateTime
import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.RootRinneLogger
import com.rinne.libraries.logger.core.model.RinneLog
import com.rinne.libraries.logger.core.model.RinneLogType
import kotlinx.coroutines.flow.StateFlow

internal class RinneRootDefaultLogger : RinneLogger {
    private val logger
        get() = RootRinneLogger.default
    override val logsStateFlow: StateFlow<List<RinneLog>>
        get() = logger.logsStateFlow
    override val defaultTag: String
        get() = logger.defaultTag

    override fun log(log: RinneLog) {
        logger.log(log = log)
    }

    override fun log(
        message: String,
        type: RinneLogType,
        tag: String,
        dateTime: RinneDateTime
    ) {
        logger.log(message = message, type = type, tag = tag, dateTime = dateTime)
    }

    override fun clearLogs() {
        logger.clearLogs()
    }
}