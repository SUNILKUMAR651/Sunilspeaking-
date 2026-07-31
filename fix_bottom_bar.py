import re

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "r") as f:
    content = f.read()

old_bottom_bar = """                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4EE6)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp).widthIn(min = 200.dp)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Speak")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tap to speak", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFE5E5),
                        modifier = Modifier.size(56.dp)
                    ) {"""

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
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) Color(0xFFFF4B4B) else Color(0xFF6B4EE6)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp).widthIn(min = 200.dp)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Speak")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isRecording) "Listening..." else "Tap to speak", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFE5E5),
                        modifier = Modifier.size(56.dp).clickable { recognizedText = "" }
                    ) {"""

content = content.replace(old_bottom_bar, new_bottom_bar)

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "w") as f:
    f.write(content)
