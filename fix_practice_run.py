path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, 'r') as f: content = f.read()

# Add tts and useFemaleVoice as parameters to PracticeMainPanel
content = content.replace("fun PracticeMainPanel(sentence: String, currentIndex: Int, totalCount: Int, modifier: Modifier = Modifier) {", "fun PracticeMainPanel(sentence: String, currentIndex: Int, totalCount: Int, tts: TextToSpeech?, useFemaleVoice: Boolean, modifier: Modifier = Modifier) {")

# In PracticeRunScreen, when it calls PracticeMainPanel, pass them.
# PracticeMainPanel(sentence = sentences[currentSentenceIndex], currentIndex = currentSentenceIndex + 1, totalCount = sentences.size)
content = content.replace("PracticeMainPanel(sentence = sentences[currentSentenceIndex], currentIndex = currentSentenceIndex + 1, totalCount = sentences.size)", "PracticeMainPanel(sentence = sentences[currentSentenceIndex], currentIndex = currentSentenceIndex + 1, totalCount = sentences.size, tts = tts, useFemaleVoice = userProfile.useFemaleVoice)")

content = content.replace("userProfile.useFemaleVoice", "useFemaleVoice")
# But restore it where it needs userProfile:
content = content.replace("useFemaleVoice = useFemaleVoice", "useFemaleVoice = userProfile.useFemaleVoice")
content = content.replace("val userProfile by viewModel.useFemaleVoice.collectAsState()", "val userProfile by viewModel.userProfile.collectAsState()")
content = content.replace("userProfile.name", "userProfile.name")
# Actually, replacing "userProfile.useFemaleVoice" inside PracticeMainPanel is enough.
with open(path, 'w') as f: f.write(content)

