import re

path = "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
with open(path, "r") as f:
    content = f.read()

old_catch = """                        try {
                            val userContent = Content(listOf(Part("User: $text")))
                            val newHistory = conversationHistory + userContent
                            
                            val systemPrompt = "You are an English teacher having a voice call with a student. Keep your answers short, conversational, and helpful (max 2-3 sentences)."
                            
                            val request = GenerateContentRequest(
                                contents = newHistory,
                                systemInstruction = Content(listOf(Part(systemPrompt)))
                            )
                            
                            val responseObj = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                            val aiResponse = responseObj.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I didn't hear that properly."
                            
                            val cleanResponse = aiResponse.replace(Regex("^(AI: )"), "").replace(Regex("^(Teacher: )"), "")
                            aiMessage = cleanResponse
                            conversationHistory = newHistory + Content(listOf(Part("Teacher: $cleanResponse")))
                            
                            tts?.speakWithVoice(cleanResponse, userProfile.useFemaleVoice, object : android.speech.tts.UtteranceProgressListener() {
                                override fun onStart(utteranceId: String?) { isSpeaking = true }
                                override fun onDone(utteranceId: String?) { 
                                    isSpeaking = false 
                                    if(!isMuted) startListening()
                                }
                                override fun onError(utteranceId: String?) { 
                                    isSpeaking = false
                                    if(!isMuted) startListening()
                                }
                            })
                        } catch (e: Exception) {
                            aiMessage = "Connection error. Trying to reconnect..."
                            delay(2000)
                            aiMessage = "Connected. Tap mic to speak."
                        }"""

new_catch = """                        try {
                            val userContent = Content(listOf(Part("User: $text")))
                            val newHistory = conversationHistory + userContent
                            
                            val systemPrompt = "You are an English teacher having a voice call with a student. Keep your answers short, conversational, and helpful (max 2-3 sentences)."
                            
                            val request = GenerateContentRequest(
                                contents = newHistory,
                                systemInstruction = Content(listOf(Part(systemPrompt)))
                            )
                            
                            var retryCount = 0
                            var aiResponse = ""
                            while(retryCount < 3) {
                                try {
                                    val responseObj = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                                    aiResponse = responseObj.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I didn't hear that properly."
                                    break
                                } catch (e: Exception) {
                                    retryCount++
                                    if (retryCount >= 3) throw e
                                    kotlinx.coroutines.delay(1000L * retryCount)
                                }
                            }
                            
                            val cleanResponse = aiResponse.replace(Regex("^(AI: )"), "").replace(Regex("^(Teacher: )"), "")
                            aiMessage = cleanResponse
                            conversationHistory = newHistory + Content(listOf(Part("Teacher: $cleanResponse")))
                            
                            tts?.speakWithVoice(cleanResponse, userProfile.useFemaleVoice, object : android.speech.tts.UtteranceProgressListener() {
                                override fun onStart(utteranceId: String?) { isSpeaking = true }
                                override fun onDone(utteranceId: String?) { 
                                    isSpeaking = false 
                                    if(!isMuted) startListening()
                                }
                                override fun onError(utteranceId: String?) { 
                                    isSpeaking = false
                                    if(!isMuted) startListening()
                                }
                            })
                        } catch (e: Exception) {
                            val fallbackResponse = "I'm having a little trouble hearing you due to the connection. Could you say that again?"
                            aiMessage = fallbackResponse
                            tts?.speakWithVoice(fallbackResponse, userProfile.useFemaleVoice, object : android.speech.tts.UtteranceProgressListener() {
                                override fun onStart(utteranceId: String?) { isSpeaking = true }
                                override fun onDone(utteranceId: String?) { 
                                    isSpeaking = false 
                                    if(!isMuted) startListening()
                                }
                                override fun onError(utteranceId: String?) { 
                                    isSpeaking = false
                                    if(!isMuted) startListening()
                                }
                            })
                        }"""

content = content.replace(old_catch, new_catch)

with open(path, "w") as f:
    f.write(content)
