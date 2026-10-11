package com.rinne.libraries.error.core

internal actual fun Throwable.isFatal(): Boolean = this is VirtualMachineError || this is LinkageError
