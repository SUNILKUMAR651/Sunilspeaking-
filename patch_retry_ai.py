import re

path1 = "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt"
with open(path1, "r") as f:
    content1 = f.read()

old_catch1 = """                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't process that."
                val cleanResponse = aiResponse.replace(Regex("^(Teacher: )"), "")
                
                val finalMessages = newMessages + TeacherMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                viewModel.recordLessonCompletion(5, "vocabulary")
            } catch (e: Exception) {
                messages = messages + TeacherMessage("Sorry, I had an error connecting. Please try again.", false)
                Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
            }"""

new_catch1 = """                var retryCount = 0
                var aiResponse = ""
                while(retryCount < 3) {
                    try {
                        val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                        aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't process that."
                        break
                    } catch (e: Exception) {
                        retryCount++
                        if (retryCount >= 3) throw e
                        kotlinx.coroutines.delay(1000L * retryCount)
                    }
                }
                
                val cleanResponse = aiResponse.replace(Regex("^(Teacher: )"), "")
                
                val finalMessages = newMessages + TeacherMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                viewModel.recordLessonCompletion(5, "vocabulary")
            } catch (e: Exception) {
                // Fallback smooth message instead of an ugly error
                val fallbackResponse = "I seem to be having a little trouble connecting to my knowledge base right now. Let's keep practicing our English! What else would you like to talk about?"
                val finalMessages = newMessages + TeacherMessage(fallbackResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
            }"""

content1 = content1.replace(old_catch1, new_catch1)

with open(path1, "w") as f:
    f.write(content1)

path2 = "app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt"
with open(path2, "r") as f:
    content2 = f.read()

old_catch2 = """                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I didn't quite catch that."
                val cleanResponse = aiResponse.replace(Regex("^(AI: )"), "")
                
                val finalMessages = newMessages + RoleplayMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                
                tts?.speakWithVoice(cleanResponse, userProfile.useFemaleVoice, null)
                viewModel.recordLessonCompletion(5, "speaking")
            } catch (e: Exception) {
                messages = messages + RoleplayMessage("Sorry, I had an error connecting.", false)
            }"""

new_catch2 = """                var retryCount = 0
                var aiResponse = ""
                while(retryCount < 3) {
                    try {
                        val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                        aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I didn't quite catch that."
                        break
                    } catch (e: Exception) {
                        retryCount++
                        if (retryCount >= 3) throw e
                        kotlinx.coroutines.delay(1000L * retryCount)
                    }
                }
                
                val cleanResponse = aiResponse.replace(Regex("^(AI: )"), "")
                
                val finalMessages = newMessages + RoleplayMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                
                tts?.speakWithVoice(cleanResponse, userProfile.useFemaleVoice, null)
                viewModel.recordLessonCompletion(5, "speaking")
            } catch (e: Exception) {
                // Smooth fallback response
                val fallbackResponse = "I'm having a bit of trouble with my connection, but let's continue! Could you repeat that or say something else?"
                val finalMessages = newMessages + RoleplayMessage(fallbackResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                tts?.speakWithVoice(fallbackResponse, userProfile.useFemaleVoice, null)
            }"""

content2 = content2.replace(old_catch2, new_catch2)

with open(path2, "w") as f:
    f.write(content2)
