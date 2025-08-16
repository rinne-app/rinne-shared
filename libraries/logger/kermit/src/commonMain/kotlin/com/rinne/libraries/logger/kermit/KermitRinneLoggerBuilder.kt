package com.rinne.libraries.logger.kermit

import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.RinneLoggerBuilder
import com.rinne.libraries.logger.kermit.impl.KermitRinneLoggerImpl

class KermitRinneLoggerBuilder(
    private var defaultTag: String = RinneLogger.DEFAULT_TAG,
) : RinneLoggerBuilder {

    override fun setDefaultTag(tag: String): RinneLoggerBuilder {
        defaultTag = tag

        return this
    }

    override fun build(): RinneLogger {
        return KermitRinneLoggerImpl(defaultTag)
    }
}