package com.rinne.libraries.logger.core

import com.rinne.libraries.logger.core.impl.RinneLoggerProviderImpl

object RootRinneLogger {
    val provider by lazy<RinneLoggerProvider> { RinneLoggerProviderImpl() }

    var default: RinneLogger = EmptyRinneLogger
        private set

    fun createNewLogger(defaultTag: String = RinneLogger.DEFAULT_TAG) =
        provider.builder.setDefaultTag(defaultTag).build()

    fun setDefaultLogger(logger: RinneLogger) {
        default = logger
    }

    fun provideAndCreate(
        builder: RinneLoggerBuilder,
        defaultTag: String = RinneLogger.DEFAULT_TAG,
    ): RinneLogger {
        provider.provide(builder)

        return createNewLogger(defaultTag)
    }
}
