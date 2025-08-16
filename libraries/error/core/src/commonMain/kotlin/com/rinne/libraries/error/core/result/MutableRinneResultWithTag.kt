package com.rinne.libraries.error.core.result

import com.rinne.libraries.error.core.result.impl.MutableRinneResultWithTagImpl
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun <T> MutableAppResultWithTag(
    state: RinneResultState<T> = RinneResultState.Loading
): MutableRinneResultWithTag<T> = MutableRinneResultWithTagImpl(state)

fun <T> MutableAppResultWithTag(
    data: T,
): MutableRinneResultWithTag<T> = MutableRinneResultWithTagImpl(RinneResultState.Success(data))

interface MutableRinneResultWithTag<T> : MutableRinneResult<T> {
    @OptIn(ExperimentalUuidApi::class)
    fun setState(newState: RinneResultState<T>, tag: String = Uuid.random().toString())
}

fun <T> MutableRinneResultWithTag<T>.setLoading(tag: String) {
    setState(RinneResultState.Loading, tag)
}