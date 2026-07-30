package com.example.utils

import android.media.MediaPlayer

class AudioPlayer {
    private var player: MediaPlayer? = null
    
    fun play(url: String, onCompletion: () -> Unit) {
        player?.release()
        player = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            setOnCompletionListener { 
                onCompletion()
                release()
            }
            start()
        }
    }
    
    fun stop() {
        player?.stop()
        player?.release()
        player = null
    }
}
