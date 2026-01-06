package com.rinne.libraries.error.core.result.observer

import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.error.core.result.refreshable.RinneRefreshable

interface RinneResultRefreshableObserver<T> : RinneResultObserver<T>, RinneRefreshable

fun <T> RinneResultRefreshableObserver(
    refresh: () -> Unit,
    initialValue: RinneResultState<T> = RinneResultState.None,
    block: suspend RinneObserverScope<RinneResultState<T>>.() -> Unit
): RinneResultRefreshableObserver<T> = RinneResultRefreshableObserverImpl(
    refresh = refresh,
    observer = RinneResultObserverImpl(initialValue = initialValue, block = block),
)

internal class RinneResultRefreshableObserverImpl<T>(
    private val refresh: () -> Unit,
    observer: RinneResultObserver<T>,
) : RinneResultRefreshableObserver<T>, RinneResultObserver<T> by observer {

    override fun refresh() {
        refresh.invoke()
    }
}