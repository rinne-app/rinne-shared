package com.rinne.libraries.error.core

fun interface RinneIsAppLevelErrorProvider {
    fun isAppLevelError(exception: RinneException): Boolean
}

//TODO example
private class IsAppLevelErrorProvider : RinneIsAppLevelErrorProvider {
    override fun isAppLevelError(exception: RinneException): Boolean {
        return when (exception) {
            is RinneKtorClient.Unauthorized -> true
            else -> false
        }
    }
}