import re
path = "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
with open(path, "r") as f:
    content = f.read()

# Add import for FishAudioService
if "import com.example.api.FishAudioService" not in content:
    content = content.replace("import com.example.utils.speakWithVoice", "import com.example.utils.speakWithVoice\nimport com.example.api.FishAudioService\nimport android.widget.Toast")

# Remove TTS initialization and utterance progress listener
content = re.sub(
    r'val ttsInstance = TextToSpeech\(context\) \{ status ->.*?\}\s*\}\s*tts\.value = ttsInstance',
    "",
    content,
    flags=re.DOTALL
)

# Remove the tts related code from cleanup
content = re.sub(
    r'DisposableEffect.*?tts\.value\?\.stop\(\).*?tts\.value\?\.shutdown\(\).*?\}',
    "",
    content,
    flags=re.DOTALL
)

# Replace the TTS speak call in the try-catch block
old_speak_1 = """                            isSpeaking = true
                            tts.value?.let { ttsInstance ->
                                if (isFemaleVoice) {
                                    ttsInstance.setPitch(1.4f)
                                } else {
                                    ttsInstance.setPitch(0.7f)
                                }
                                val utteranceId = "response_${System.currentTimeMillis()}"
                                ttsInstance.speakWithVoice(cleanResponse, userProfile.useFemaleVoice, utteranceId)
                            }"""

new_speak_1 = """                            isSpeaking = true
                            coroutineScope.launch {
                                FishAudioService.speak(
                                    context = context,
                                    text = cleanResponse,
                                    apiKey = "cb4905e8b23a48b5b1ee3946315e2403",
                                    onStart = {
                                        isSpeaking = true
                                    },
                                    onComplete = {
                                        isSpeaking = false
                                        aiMessage = "..."
                                        startListening()
                                    },
                                    onError = { error ->
                                        isSpeaking = false
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        aiMessage = "..."
                                        startListening()
                                    }
                                )
                            }"""

content = content.replace(old_speak_1, new_speak_1)

with open(path, "w") as f:
    f.write(content)
