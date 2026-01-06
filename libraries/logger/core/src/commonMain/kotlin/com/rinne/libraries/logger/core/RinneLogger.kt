package com.rinne.libraries.logger.core

import com.rinne.libraries.date.time.core.RinneDateTime
import com.rinne.libraries.logger.core.combined.CombinedRinneLogger
import com.rinne.libraries.logger.core.impl.RinneRootDefaultLogger
import com.rinne.libraries.logger.core.model.RinneLog
import com.rinne.libraries.logger.core.model.RinneLogType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.ExperimentalTime

interface RinneLogger {
    val logsStateFlow: StateFlow<List<RinneLog>>
    val defaultTag: String

    fun log(log: RinneLog)

    @OptIn(ExperimentalTime::class)
    fun log(
        message: String,
        type: RinneLogType,
        tag: String = defaultTag,
        dateTime: RinneDateTime = RinneDateTime.now(),
    )

    fun clearLogs()

    companion object : RinneLogger by RinneRootDefaultLogger() {
        const val DEFAULT_TAG = "RinneLogger"
    }
}

@Suppress("FunctionName")
fun RinneLogger.Companion.Combined(
    loggers: List<RinneLogger>,
    mainLogger: RinneLogger = RootRinneLogger.createNewLogger(),
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob())
): RinneLogger = CombinedRinneLogger(
    mainLogger = mainLogger,
    loggers = loggers,
    coroutineScope = coroutineScope,
)

