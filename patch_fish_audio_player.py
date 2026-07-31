import re
path = "app/src/main/java/com/example/utils/FishAudioPlayer.kt"
with open(path, "r") as f:
    content = f.read()

bad = """    private suspend fun fallback(text: String, isFemale: Boolean, tts: TextToSpeech?, utteranceId: String?) {
        withContext(Dispatchers.Main) {
            tts?.speakWithVoice(text, isFemale, utteranceId)
        }
    }"""

good = """    private suspend fun fallback(text: String, isFemale: Boolean, tts: TextToSpeech?, utteranceId: String?, onStart: (() -> Unit)?, onDone: (() -> Unit)?) {
        withContext(Dispatchers.Main) {
            if (tts == null) {
                onDone?.invoke()
                return@withContext
            }
            onStart?.invoke()
            
            // Set up a temporary listener for this utterance if needed
            val listener = object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(id: String?) {}
                override fun onDone(id: String?) {
                    if (id == utteranceId) {
                        onDone?.invoke()
                    }
                }
                @Deprecated("Deprecated in Java")
                override fun onError(id: String?) {
                    if (id == utteranceId) {
                        onDone?.invoke()
                    }
                }
            }
            tts.setOnUtteranceProgressListener(listener)
            tts.speakWithVoice(text, isFemale, utteranceId)
        }
    }"""

content = content.replace(bad, good)

# Also update the calls to fallback
content = content.replace("fallback(text, isFemale, fallbackTts, utteranceId)", "fallback(text, isFemale, fallbackTts, utteranceId, onStart, onDone)")

with open(path, "w") as f:
    f.write(content)

