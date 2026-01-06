package com.rinne.libraries.tts.core

import androidx.compose.runtime.Composable
import com.rinne.libraries.locale.core.RinneLocale
import org.koin.core.Koin

//TODO desktop
actual fun createDefaultTtsManager(context: RinneTtsManagerContext): RinneTtsManager {
    return object : RinneTtsManager {
        override fun setLanguage(locale: RinneLocale) {}

        override fun setText(text: String?) {}

        override fun play() {}

        override suspend fun playWithDelay(text: List<String>) {}

        override fun stop() {}
    }
}

actual fun Koin.getTtsManagerContext(): RinneTtsManagerContext {
    return object : RinneTtsManagerContext {}
}

@Composable
actual fun getTtsManagerContext(): RinneTtsManagerContext {
    return object : RinneTtsManagerContext {}
}