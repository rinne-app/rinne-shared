package com.rinne.libraries.logger.core

interface RinneLoggerProvider {
    val builder: RinneLoggerBuilder

    fun provide(builder: RinneLoggerBuilder)
}

