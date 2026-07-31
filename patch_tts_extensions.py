import re
with open("app/src/main/java/com/example/utils/TtsExtensions.kt", "r") as f:
    content = f.read()

bad = """fun TextToSpeech?.speakWithVoice(text: String, isFemale: Boolean, utteranceId: String? = null, audioEnabled: Boolean = true) {
    if (this == null || !audioEnabled) return
    
    val voiceObj = voices?.firstOrNull { 
        val name = it.name.lowercase()
        if (isFemale) name.contains("female") else name.contains("male") 
    }
    
    if (voiceObj != null) {
        voice = voiceObj
    }
    
    speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
}"""

good = """fun TextToSpeech?.speakWithVoice(text: String, isFemale: Boolean, utteranceId: String? = null, audioEnabled: Boolean = true) {
    if (this == null || !audioEnabled) return
    
    val voiceObj = voices?.firstOrNull { 
        val name = it.name.lowercase()
        if (isFemale) name.contains("female") else name.contains("male") 
    }
    
    if (voiceObj != null) {
        voice = voiceObj
    }
    
    // Adjust pitch and rate to make standard TTS sound slightly better
    if (isFemale) {
        setPitch(1.1f)
        setSpeechRate(0.95f)
    } else {
        setPitch(0.9f)
        setSpeechRate(0.95f)
    }
    
    speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
}"""

if bad in content:
    content = content.replace(bad, good)
    with open("app/src/main/java/com/example/utils/TtsExtensions.kt", "w") as f:
        f.write(content)
