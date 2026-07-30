package com.example.utils

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    var currentFile: File? = null
    
    fun startRecording() {
        val fileName = "recording_${System.currentTimeMillis()}.3gp"
        currentFile = File(context.cacheDir, fileName)
        
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(currentFile!!.absolutePath)
            prepare()
            start()
        }
    }
    
    fun stopRecording(): File? {
        try {
            recorder?.stop()
            recorder?.release()
        } catch(e: Exception) {}
        recorder = null
        return currentFile
    }
}
