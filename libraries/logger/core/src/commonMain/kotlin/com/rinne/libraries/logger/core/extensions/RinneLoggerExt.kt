package com.rinne.libraries.logger.core.extensions

import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.model.RinneLogType

fun RinneLogger.w(
    message: String,
    tag: String = defaultTag
) = log(message = message, tag = tag, type = RinneLogType.Warning)

fun RinneLogger.i(
    message: String,
    tag: String = defaultTag
) = log(message = message, tag = tag, type = RinneLogType.Info)

fun RinneLogger.d(
    message: String,
    tag: String = defaultTag
) = log(message = message, tag = tag, type = RinneLogType.Debug)

fun RinneLogger.e(
    message: String = "",
    throwable: Throwable? = null,
    tag: String = defaultTag
) = log(message = message, tag = tag, type = RinneLogType.Error(throwable))