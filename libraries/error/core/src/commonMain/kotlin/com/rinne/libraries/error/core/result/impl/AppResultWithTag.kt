package com.rinne.libraries.error.core.result.impl

import com.rinne.libraries.error.core.result.MutableRinneResultWithTag
import com.rinne.libraries.error.core.result.RinneResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class MutableRinneResultWithTagImpl<T>(
    state: RinneResultState<T>,
) : MutableRinneResultWithTag<T> {
    val activeTagsStateFlow = MutableStateFlow<Set<String>>(setOf())

    private val _stateFlow = MutableStateFlow<RinneResultState<T>>(state)
    override val stateFlow = _stateFlow.asStateFlow()

    //TODO maybe improve for cncurrency
    override fun setState(newState: RinneResultState<T>, tag: String) {
        activeTagsStateFlow.update {
            if (newState is RinneResultState.Loading) it + tag else it.minus(tag)
        }

        _stateFlow.update { state ->
            if (activeTagsStateFlow.value.isEmpty()) newState else RinneResultState.Loading
        }
    }

    override fun setState(newState: RinneResultState<T>) {
        setState(newState = newState, tag = DEFAULT_TAG)
    }

    companion object {
        private const val DEFAULT_TAG = ""
    }
}