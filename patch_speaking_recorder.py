import re

path = "app/src/main/java/com/example/ui/speaking/SpeakingEvaluationScreen.kt"
with open(path, "r") as f:
    content = f.read()

import_str = """import androidx.compose.ui.text.font.FontWeight"""

new_import_str = """import androidx.compose.ui.text.font.FontWeight
import com.example.utils.AudioRecorder
import java.io.File"""

content = content.replace(import_str, new_import_str)

decl = """    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }"""

new_decl = """    val audioRecorder = remember { AudioRecorder(context) }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }"""

content = content.replace(decl, new_decl)

old_results = """                    val score = if (targetWords.isEmpty()) 0 else (matches.toFloat() / targetWords.size * 100).toInt().coerceIn(0, 100)
                    
                    isSuccess = score >= 70
                }
            }"""

new_results = """                    val score = if (targetWords.isEmpty()) 0 else (matches.toFloat() / targetWords.size * 100).toInt().coerceIn(0, 100)
                    
                    isSuccess = score >= 70
                    
                    val file = audioRecorder.stopRecording()
                    if (file != null && file.exists()) {
                        viewModel.uploadRecording(file, currentSentence, score)
                    }
                    
                    // Add wrong words to Weak Words
                    targetWords.forEach { word ->
                        val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                        if (!recognizedWordsList.contains(cleanWord)) {
                            viewModel.addWord(
                                com.example.data.WordObject(
                                    word = cleanWord,
                                    phonetic = "",
                                    partOfSpeech = "word",
                                    definitions = listOf(com.example.data.Definition("Missed during speaking practice", "Sentence: $currentSentence")),
                                    collocations = emptyList(),
                                    idioms = emptyList(),
                                    formalUsage = "",
                                    informalUsage = "",
                                    slangUsage = "",
                                    memoryHook = "Practice pronunciation",
                                    physicalAction = "",
                                    mastery = com.example.data.MasteryExercise("", "", ""),
                                    category = "Speaking Mistakes",
                                    easinessFactor = 1.3f
                                )
                            )
                        }
                    }
                }
            }"""

content = content.replace(old_results, new_results)

old_error = """            override fun onError(error: Int) { isRecording = false }"""

new_error = """            override fun onError(error: Int) { 
                isRecording = false 
                audioRecorder.stopRecording()
            }"""

content = content.replace(old_error, new_error)

old_stop = """                            speechRecognizer.stopListening()
                            isRecording = false"""

new_stop = """                            speechRecognizer.stopListening()
                            audioRecorder.stopRecording()
                            isRecording = false"""

content = content.replace(old_stop, new_stop)

old_start = """                            recognizedText = ""
                            speechRecognizer.startListening(speechRecognizerIntent)
                            isRecording = true"""

new_start = """                            recognizedText = ""
                            try { audioRecorder.startRecording() } catch (e: Exception) {}
                            speechRecognizer.startListening(speechRecognizerIntent)
                            isRecording = true"""

content = content.replace(old_start, new_start)

with open(path, "w") as f:
    f.write(content)
