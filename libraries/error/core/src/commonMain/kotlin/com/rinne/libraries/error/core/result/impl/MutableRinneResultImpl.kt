package com.rinne.libraries.error.core.result.impl

import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.error.core.result.MutableRinneResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class MutableRinneResultImpl<T>(state: RinneResultState<T>) : MutableRinneResult<T> {
    private val _stateFlow = MutableStateFlow<RinneResultState<T>>(state)
    override val stateFlow = _stateFlow.asStateFlow()

    override fun setState(newState: RinneResultState<T>) {
        _stateFlow.value = newState
    }
}