package com.rinne.libraries.logger.core

import com.rinne.libraries.logger.core.model.RinneLog
import com.rinne.libraries.logger.core.model.RinneLogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDateTime

object EmptyRinneLogger : RinneLogger {
    override val logsStateFlow: StateFlow<List<RinneLog>> = MutableStateFlow(emptyList())
    override val defaultTag: String = "EmptyRinneLogger"

    override fun log(log: RinneLog) {}

    override fun log(
        message: String,
        type: RinneLogType,
        tag: String,
        dateTime: LocalDateTime
    ) {
    }

    override fun clearLogs() {}
}