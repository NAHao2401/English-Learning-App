package com.example.englishlearningapp.features.vocab.ui

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Plays vocab audio from URL with TextToSpeech fallback.
 * Call release() in DisposableEffect onDispose.
 */
class VocabAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                ttsReady = true
            }
        }
    }

    /**
     * Play from URL (e.g. "static/audio/words/house.mp3").
     * baseUrl must end with "/" (e.g. "http://192.168.1.5:8000/")
     * Falls back to TTS with [fallbackText] if URL is null/empty.
     */
    fun play(audioUrl: String?, baseUrl: String, fallbackText: String) {
        stopCurrent()

        if (!audioUrl.isNullOrBlank()) {
            val fullUrl = if (audioUrl.startsWith("http")) audioUrl
            else "$baseUrl$audioUrl"
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(fullUrl)
                    setOnPreparedListener { start() }
                    setOnCompletionListener { release() }
                    setOnErrorListener { _, _, _ ->
                        // Fallback to TTS on error
                        speakTTS(fallbackText)
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                speakTTS(fallbackText)
            }
        } else {
            speakTTS(fallbackText)
        }
    }

    private fun speakTTS(text: String) {
        if (ttsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stopCurrent() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) stop()
            } catch (_: Exception) { }
            release()
        }
        mediaPlayer = null
        tts?.stop()
    }

    fun release() {
        stopCurrent()
        tts?.shutdown()
        tts = null
    }
}

@Composable
fun rememberVocabAudioPlayer(): VocabAudioPlayer {
    val context = LocalContext.current
    val player = remember { VocabAudioPlayer(context) }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    return player
}
