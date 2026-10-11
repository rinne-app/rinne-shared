package com.rinne.libraries.error.core

import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RinneCatchingTest {

    @Test
    fun `a value is returned as Right`() {
        assertEquals(42.right(), rinneCatching { 42 })
    }

    @Test
    fun `an exception becomes Unexpected`() {
        assertEquals(CommonError.Unexpected.left(), rinneCatching { error("boom") })
    }

    @Test
    fun `cancellation is rethrown`() {
        assertFailsWith<CancellationException> { rinneCatching { throw CancellationException("cancelled") } }
    }

    @Test
    fun `a Left from the block stays as it is`() {
        val result = rinneCatchingEither<Int> { CommonError.Offline.left() }
        assertEquals(CommonError.Offline.left(), result)
    }

    @Test
    fun `a flow failure ends with one Unexpected`() = runTest {
        val values = flow {
            emit(1)
            error("boom")
        }.catchAsEither().toList()
        assertEquals(listOf(1.right(), CommonError.Unexpected.left()), values)
    }

    @Test
    fun `Either values pass through catchUnexpected`() = runTest {
        val values = flowOf(1.right(), CommonError.Offline.left()).catchUnexpected().toList()
        assertEquals(listOf(1.right(), CommonError.Offline.left()), values)
    }

    @Test
    fun `a wrapped common error is unwrapped`() {
        data class Wrapped(override val error: CommonError) : WrapsCommonError
        assertEquals(CommonError.Offline, Wrapped(CommonError.Offline).unwrapCommon())
        assertEquals(CommonError.Unexpected, CommonError.Unexpected.unwrapCommon())
    }
}
