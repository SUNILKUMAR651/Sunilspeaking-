import re
with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "r") as f:
    content = f.read()

# Add imports
if "import android.speech.tts.TextToSpeech" not in content:
    content = content.replace("import android.speech.SpeechRecognizer", "import android.speech.SpeechRecognizer\nimport android.speech.tts.TextToSpeech\nimport com.example.utils.FishAudioPlayer")

# Add tts state and cleanup
if "val tts = remember" not in content:
    tts_code = """
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale.US
            }
        }
        tts.value = ttsInstance
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
            FishAudioPlayer.stop()
        }
    }
"""
    # Insert after `val coroutineScope = rememberCoroutineScope()`
    content = content.replace("val coroutineScope = rememberCoroutineScope()", "val coroutineScope = rememberCoroutineScope()" + tts_code)

# Find where AI responds
ai_response_block = """val finalMessages = newMessages + TeacherMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                viewModel.recordLessonCompletion(5, "vocabulary")"""

if ai_response_block in content:
    new_ai_response_block = ai_response_block + """
                FishAudioPlayer.playAudio(
                    context = context,
                    text = cleanResponse,
                    isFemale = userProfile.useFemaleVoice,
                    fallbackTts = tts.value
                )"""
    content = content.replace(ai_response_block, new_ai_response_block)

# Also find where mock response is generated
mock_response_block = """val finalMessages = newMessages + TeacherMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)"""
                
# Wait, let's check what the catch block looks like
