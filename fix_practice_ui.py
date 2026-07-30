import re

path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, "r") as f:
    content = f.read()

# Fix the second call to PracticeMainPanel
old_second_call = "PracticeMainPanel(sentences[currentSentenceIndex], currentSentenceIndex + 1, sentences.size, tts, userProfile.useFemaleVoice, modifier = Modifier.weight(1f))"
new_second_call = """PracticeMainPanel(
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
                        },
                        modifier = Modifier.weight(1f)
                    )"""
content = content.replace(old_second_call, new_second_call)

# Fix bottom action bar
old_bottom_bar = """                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4EE6)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp).widthIn(min = 200.dp)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tap to speak", color = Color.White, fontWeight = FontWeight.Bold)
                    }"""

new_bottom_bar = """                    Button(
                        onClick = { 
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
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) Color.Red else Color(0xFF6B4EE6)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp).widthIn(min = 200.dp)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isRecording) "Listening..." else "Tap to speak", color = Color.White, fontWeight = FontWeight.Bold)
                    }"""
content = content.replace(old_bottom_bar, new_bottom_bar)


# Fix PracticeMainPanel to use recognizedText and logic
old_main_panel = """                        Text("Waiting for your voice...", color = Color.Gray, fontSize = 16.sp)"""
new_main_panel = """                        Text(if (recognizedText.isEmpty()) "Waiting for your voice..." else recognizedText, color = Color.DarkGray, fontSize = 16.sp)"""
content = content.replace(old_main_panel, new_main_panel)

with open(path, "w") as f:
    f.write(content)
