package com.rinne.libraries.di.core

interface RinneDiModule {
    var createAtStart: Boolean
}

interface RinneDiDependency {
    val createAtStart: Boolean
}

interface RinneDiProvider<T> {
    fun provide(): T
}

class DraftViewModelDependencies(
    val testUseCase: RinneDiProvider<Unit>,
    val test2UseCase: RinneDiProvider<Unit>,
)