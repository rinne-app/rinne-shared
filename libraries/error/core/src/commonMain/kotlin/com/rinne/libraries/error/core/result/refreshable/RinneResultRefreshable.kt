package com.rinne.libraries.error.core.result.refreshable

import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.RinneResult
import com.rinne.libraries.error.core.result.extensions.launchWithResult
import kotlinx.coroutines.CoroutineScope

interface RinneResultRefreshable<T> : RinneRefreshable, RinneResult<T>

fun <T> RinneResultRefreshable(
    coroutineScope: CoroutineScope,
    block: suspend CoroutineScope.() -> T,
): RinneResultRefreshable<T> = RinneResultRefreshableImpl(coroutineScope, block)

private class RinneResultRefreshableImpl<T>(
    private val coroutineScope: CoroutineScope,
    private val block: suspend CoroutineScope.() -> T,
) : RinneResultRefreshable<T> {
    private val mutableRinneResult = MutableRinneResult<T>()
    override val stateFlow = mutableRinneResult.stateFlow

    override fun refresh() {
        coroutineScope.launchWithResult(mutableRinneResult) {
            block()
        }
    }
}