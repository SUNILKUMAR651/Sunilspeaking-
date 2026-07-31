with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "r") as f:
    content = f.read()

mock_response_block = """val fallbackResponse = mockResponses.random()
                val finalMessages = newMessages + TeacherMessage(fallbackResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)"""

if mock_response_block in content:
    new_mock_response_block = mock_response_block + """
                FishAudioPlayer.playAudio(
                    context = context,
                    text = fallbackResponse,
                    isFemale = userProfile.useFemaleVoice,
                    fallbackTts = tts.value
                )"""
    content = content.replace(mock_response_block, new_mock_response_block)

with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "w") as f:
    f.write(content)
