package com.rinne.libraries.tts.core

import androidx.compose.runtime.Composable
import org.koin.core.Koin

//TODO
enum class RinneTtsLanguage {
    ENGLISH,
    GERMAN,
    RUSSIAN,
}

internal expect fun createDefaultTtsManager(context: RinneTtsManagerContext): RinneTtsManager

interface RinneTtsManager {
    fun setLanguage(language: RinneTtsLanguage)
    fun setText(text: String?)
    fun play()
    suspend fun playWithDelay(text: List<String>)
    fun stop()

    companion object {
        @Suppress("FunctionName")
        fun Default(context: RinneTtsManagerContext) = createDefaultTtsManager(context)
    }
}

interface RinneTtsManagerContext

expect fun Koin.getTtsManagerContext(): RinneTtsManagerContext

@Composable
expect fun getTtsManagerContext(): RinneTtsManagerContext