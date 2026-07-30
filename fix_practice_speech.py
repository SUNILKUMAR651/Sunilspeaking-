import re

path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, "r") as f:
    content = f.read()

# 1. Add imports
imports_to_add = """
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import java.util.Locale
"""
if "import android.speech.SpeechRecognizer" not in content:
    content = content.replace("import android.speech.tts.TextToSpeech", imports_to_add.strip() + "\nimport android.speech.tts.TextToSpeech")

# 2. Add state inside PracticeRunScreen
state_to_add = """
    var hasMicPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) }
    
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasMicPermission = isGranted
    }

    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isRecording = false }
            override fun onError(error: Int) { isRecording = false }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
                isRecording = false
            }
            override fun onPartialResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose {
            speechRecognizer.stopListening()
            speechRecognizer.destroy()
        }
    }
"""

if "val speechRecognizer" not in content:
    # insert after val context = LocalContext.current
    content = content.replace("val context = LocalContext.current\n", "val context = LocalContext.current\n" + state_to_add)

# 3. Update the invocation of PracticeMainPanel to pass isRecording, recognizedText, etc.
if "fun PracticeMainPanel(sentence: String, currentIndex: Int, totalCount: Int, tts: TextToSpeech?, useFemaleVoice: Boolean" in content:
    # replace signature
    old_sig = "fun PracticeMainPanel(sentence: String, currentIndex: Int, totalCount: Int, tts: TextToSpeech?, useFemaleVoice: Boolean, modifier: Modifier = Modifier) {"
    new_sig = "fun PracticeMainPanel(sentence: String, currentIndex: Int, totalCount: Int, tts: TextToSpeech?, useFemaleVoice: Boolean, isRecording: Boolean, recognizedText: String, onToggleRecord: () -> Unit, onNext: () -> Unit, modifier: Modifier = Modifier) {"
    content = content.replace(old_sig, new_sig)
    
    # replace invocations in PracticeRunScreen
    old_call = "PracticeMainPanel(sentences[currentSentenceIndex], currentSentenceIndex + 1, sentences.size, tts, userProfile.useFemaleVoice)"
    new_call = """PracticeMainPanel(
                            sentence = sentences[currentSentenceIndex],
                            currentIndex = currentSentenceIndex + 1,
                            totalCount = sentences.size,
                            tts = tts,
                            useFemaleVoice = userProfile.useFemaleVoice,
                            isRecording = isRecording,
                            recognizedText = recognizedText,
                            onToggleRecord = {
                                if (isRecording) {
                                    speechRecognizer.stopListening()
                                    isRecording = false
                                } else {
                                    if (hasMicPermission) {
                                        recognizedText = ""
                                        speechRecognizer.startListening(speechRecognizerIntent)
                                        isRecording = true
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            onNext = {
                                if (currentSentenceIndex < sentences.size - 1) {
                                    currentSentenceIndex++
                                    recognizedText = ""
                                } else {
                                    onBack()
                                }
                            }
                        )"""
    content = content.replace(old_call, new_call)

with open(path, "w") as f:
    f.write(content)
