import re
path = "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
with open(path, "r") as f:
    content = f.read()

bad = """                            if (isAudioEnabled) {
                                val utteranceId = "response_${System.currentTimeMillis()}"
                                ttsInstance.speakWithVoice(cleanResponse, userProfile.useFemaleVoice, utteranceId)
                            }"""

good = """                            isSpeaking = true
                            tts.value?.let { ttsInstance ->
                                if (isFemaleVoice) {
                                    ttsInstance.setPitch(1.4f)
                                } else {
                                    ttsInstance.setPitch(0.7f)
                                }
                                val utteranceId = "response_${System.currentTimeMillis()}"
                                ttsInstance.speakWithVoice(cleanResponse, userProfile.useFemaleVoice, utteranceId)
                            }"""

content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
