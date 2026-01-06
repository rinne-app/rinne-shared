package com.rinne.libraries.tts.core

import androidx.compose.runtime.Composable
import com.rinne.libraries.locale.core.RinneLocale
import org.koin.core.Koin

internal expect fun createDefaultTtsManager(context: RinneTtsManagerContext): RinneTtsManager

interface RinneTtsManager {
    fun setLanguage(locale: RinneLocale)
    fun setText(text: String?)
    fun play()
    suspend fun playWithDelay(text: List<String>)
    fun stop()

    companion object {
        @Suppress("FunctionName")
        fun Default(context: RinneTtsManagerContext) = createDefaultTtsManager(context)

        val empty = object : RinneTtsManager {
            override fun setLanguage(locale: RinneLocale) {}
            override fun setText(text: String?) {}
            override fun play() {}
            override suspend fun playWithDelay(text: List<String>) {}
            override fun stop() {}
        }
    }
}

interface RinneTtsManagerContext

expect fun Koin.getTtsManagerContext(): RinneTtsManagerContext

@Composable
expect fun getTtsManagerContext(): RinneTtsManagerContext