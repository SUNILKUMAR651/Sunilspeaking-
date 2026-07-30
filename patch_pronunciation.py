import re

path = "app/src/main/java/com/example/ui/screens/PronunciationHistoryScreen.kt"
with open(path, "r") as f:
    content = f.read()

old_tts = """                                            tts?.speakWithVoice(item.sentence, userProfile.useFemaleVoice, object: android.speech.tts.UtteranceProgressListener() {
                                                override fun onStart(utteranceId: String?) {}
                                                override fun onDone(utteranceId: String?) { playingNativeSentence = null }
                                                override fun onError(utteranceId: String?) { playingNativeSentence = null }
                                            })"""

new_tts = """                                            tts?.setOnUtteranceProgressListener(object: android.speech.tts.UtteranceProgressListener() {
                                                override fun onStart(utteranceId: String?) {}
                                                override fun onDone(utteranceId: String?) { playingNativeSentence = null }
                                                override fun onError(utteranceId: String?) { playingNativeSentence = null }
                                            })
                                            tts?.speakWithVoice(item.sentence, userProfile.useFemaleVoice, "native_voice")"""

content = content.replace(old_tts, new_tts)

with open(path, "w") as f:
    f.write(content)
