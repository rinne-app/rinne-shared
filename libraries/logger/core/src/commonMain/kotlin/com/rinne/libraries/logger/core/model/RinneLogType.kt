package com.rinne.libraries.logger.core.model

sealed interface RinneLogType {
    data object Info : RinneLogType
    data object Warning : RinneLogType
    data object Debug : RinneLogType
    data class Error(val throwable: Throwable? = null) : RinneLogType
}
