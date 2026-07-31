with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "r") as f:
    content = f.read()

if "import com.example.utils.FishAudioPlayer" not in content:
    content = content.replace("import android.speech.SpeechRecognizer", "import android.speech.SpeechRecognizer\nimport android.speech.tts.TextToSpeech\nimport com.example.utils.FishAudioPlayer")

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
    content = content.replace("val coroutineScope = rememberCoroutineScope()", "val coroutineScope = rememberCoroutineScope()" + tts_code)

if "FishAudioPlayer.playAudio(" not in content.split("catch (e: Exception) {")[0]:
    # Insert in the try block
    target = """val finalMessages = newMessages + TeacherMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                viewModel.recordLessonCompletion(5, "vocabulary")"""
    replacement = target + """
                FishAudioPlayer.playAudio(
                    context = context,
                    text = cleanResponse,
                    isFemale = userProfile.useFemaleVoice,
                    fallbackTts = tts.value
                )"""
    content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "w") as f:
    f.write(content)
