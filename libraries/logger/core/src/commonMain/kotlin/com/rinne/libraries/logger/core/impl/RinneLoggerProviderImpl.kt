package com.rinne.libraries.logger.core.impl

import com.rinne.libraries.logger.core.RinneLoggerBuilder
import com.rinne.libraries.logger.core.RinneLoggerProvider

internal class RinneLoggerProviderImpl : RinneLoggerProvider {
    private var _builder: RinneLoggerBuilder? = null
    override val builder
        get() = requireNotNull(_builder) { "RinneLoggerBuilder is not provided" }

    override fun provide(builder: RinneLoggerBuilder) {
        _builder = builder
    }
}