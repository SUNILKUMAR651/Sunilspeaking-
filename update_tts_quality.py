path = "app/src/main/java/com/example/utils/TtsExtensions.kt"
with open(path, 'r') as f:
    content = f.read()

old_func = """fun TextToSpeech?.speakWithVoice(text: String, isFemale: Boolean, utteranceId: String? = null, audioEnabled: Boolean = true) {
    this?.setPitch(if (isFemale) 1.2f else 0.7f)
    if (audioEnabled) this?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
}"""

new_func = """fun TextToSpeech?.speakWithVoice(text: String, isFemale: Boolean, utteranceId: String? = null, audioEnabled: Boolean = true) {
    if (this != null && audioEnabled) {
        try {
            val availableVoices = this.voices
            if (availableVoices != null) {
                // Try to find a voice matching the gender preference to avoid artificial pitch distortion
                val preferredVoice = availableVoices.firstOrNull {
                    val name = it.name.lowercase()
                    if (isFemale) name.contains("female") || name.contains("f0")
                    else name.contains("male") || name.contains("m0")
                }
                
                if (preferredVoice != null) {
                    this.voice = preferredVoice
                    this.setPitch(1.0f) // Use natural pitch for professional sound
                } else {
                    // Subtle pitch adjustment if no specific voice is found
                    this.setPitch(if (isFemale) 1.05f else 0.95f)
                }
            } else {
                this.setPitch(if (isFemale) 1.05f else 0.95f)
            }
        } catch (e: Exception) {
            this.setPitch(1.0f)
        }
        
        this.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }
}"""

content = content.replace(old_func, new_func)

with open(path, 'w') as f:
    f.write(content)
