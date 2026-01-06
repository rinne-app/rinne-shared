package com.rinne.libraries.tts.core

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.rinne.libraries.locale.core.RinneLocale
import com.rinne.libraries.locale.core.asAndroidLocale
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.Koin

class AndroidRinneTtsManagerContext(
    val context: Context,
) : RinneTtsManagerContext

@Composable
actual fun getTtsManagerContext(): RinneTtsManagerContext {

    val context = LocalContext.current
    return remember(context) { AndroidRinneTtsManagerContext(context) }
}

class AndroidRinneTtsManager(private val context: AndroidRinneTtsManagerContext) : RinneTtsManager {
    private val textToSpeech by lazy {
        (TextToSpeech(context.context) {}).apply {
            setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        progressStateFlow.value = RinneTtsProgress.InProgress
                    }

                    override fun onDone(utteranceId: String?) {
                        progressStateFlow.value = RinneTtsProgress.Idle
                    }

                    override fun onError(utteranceId: String?) {
                        progressStateFlow.value = RinneTtsProgress.Idle
                    }
                })
        }
    }
    private var text: String? = null

    private val progressStateFlow = MutableStateFlow<RinneTtsProgress>(RinneTtsProgress.Idle)

    override fun setLanguage(locale: RinneLocale) {
        textToSpeech.language = locale.asAndroidLocale()
    }

    override fun setText(text: String?) {
        this.text = text
    }

    override fun play() {
        if (textToSpeech.isSpeaking || text == null) return
//        textToSpeech.setSpeechRate(1.25f)
        textToSpeech.setSpeechRate(1f)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "unique_id")
    }

    override suspend fun playWithDelay(text: List<String>) {
        text.forEach {
            setText(it)
            play()
            progressStateFlow.firstOrNull { it is RinneTtsProgress.Idle }
            delay(1000L)
        }
    }

    override fun stop() {
        textToSpeech.stop()
    }
}

sealed interface RinneTtsProgress {
    data object InProgress : RinneTtsProgress
    data object Idle : RinneTtsProgress
}

internal actual fun createDefaultTtsManager(context: RinneTtsManagerContext): RinneTtsManager {
    require(context is AndroidRinneTtsManagerContext)

    return AndroidRinneTtsManager(context)
}

actual fun Koin.getTtsManagerContext(): RinneTtsManagerContext {
    return AndroidRinneTtsManagerContext(get())
}