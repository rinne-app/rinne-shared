package com.rinne.libraries.logger.core

interface RinneLoggerBuilder {
    fun setDefaultTag(tag: String): RinneLoggerBuilder

    fun build(): RinneLogger
}