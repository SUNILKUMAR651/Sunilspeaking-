import re
path = "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
with open(path, "r") as f:
    content = f.read()

# I will recreate the listener
listener_code = """
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onError(error: Int) {
                isListening = false
                if (!isMuted && !isSpeaking) {
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(1000)
                        startListening()
                    }
                }
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val userText = matches[0]
                    aiMessage = "..."
                    isSpeaking = true
                    coroutineScope.launch {
                        try {
                            val systemPrompt = "You are a friendly AI English teacher in a voice call. Keep responses conversational, concise, and helpful. Correct mistakes gently."
                            val newHistory = conversationHistory + Content(listOf(Part(userText)), role = "user")
                            val request = GenerateContentRequest(
                                contents = newHistory,
                                systemInstruction = Content(listOf(Part(systemPrompt)))
                            )
                            val responseObj = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                            val cleanResponse = responseObj.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Sorry, I missed that."
                            
                            conversationHistory = newHistory + Content(listOf(Part(cleanResponse)), role = "model")
                            aiMessage = cleanResponse
                            
                            FishAudioService.speak(
                                context = context,
                                text = cleanResponse,
                                apiKey = "cb4905e8b23a48b5b1ee3946315e2403",
                                onStart = {
                                    isSpeaking = true
                                },
                                onComplete = {
                                    isSpeaking = false
                                    if (!isMuted) startListening()
                                },
                                onError = { error ->
                                    isSpeaking = false
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    if (!isMuted) startListening()
                                }
                            )
                        } catch (e: Exception) {
                            isSpeaking = false
                            aiMessage = "Error connecting"
                            if (!isMuted) startListening()
                        }
                    }
                } else {
                    if (!isMuted && !isSpeaking) startListening()
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        
        DisposableEffect(Unit) {
            startListening()
            onDispose {
                speechRecognizer.destroy()
            }
        }
"""

content = content.replace("    fun startListening() {\n        if (!hasRecordPermission) {\n            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)\n            return\n        }\n        if (isMuted || isSpeaking) return\n        \n        coroutineScope.launch {\n            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {\n                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)\n                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())\n            }\n            try {\n                speechRecognizer.startListening(intent)\n                isListening = true\n                aiMessage = \"Listening...\"\n            } catch (e: Exception) {\n                // Ignore\n            }\n        }\n    }\n        \n        // Animation for avatar pulse", 
"    fun startListening() {\n        if (!hasRecordPermission) {\n            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)\n            return\n        }\n        if (isMuted || isSpeaking) return\n        \n        coroutineScope.launch {\n            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {\n                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)\n                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())\n            }\n            try {\n                speechRecognizer.startListening(intent)\n                isListening = true\n                aiMessage = \"Listening...\"\n            } catch (e: Exception) {\n                // Ignore\n            }\n        }\n    }\n" + listener_code + "\n        // Animation for avatar pulse")

with open(path, "w") as f:
    f.write(content)
