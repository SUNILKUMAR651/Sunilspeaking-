import re

with open('app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt', 'r') as f:
    content = f.read()

views = ["NewWordView", "ListeningView", "SpeakingSentenceView", "TranslationView", "ArrangeWordsView", "FillInTheBlanksView", "MatchPairsView", "PhraseNarrationView"]

for view in views:
    content = re.sub(fr'fun {view}\(([^,]+), useFemaleVoice: Boolean([^)]*)\)\s*{{', fr'fun {view}(\1, useFemaleVoice: Boolean, audioEnabled: Boolean = true\2) {{', content)
    content = re.sub(fr'useFemaleVoice = userProfile.useFemaleVoice', r'useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled', content)

content = content.replace("tts?.speakWithVoice(exercise.word, useFemaleVoice, null)", "tts?.speakWithVoice(exercise.word, useFemaleVoice, null, audioEnabled)")
content = content.replace("tts?.speakWithVoice(exercise.audioText, useFemaleVoice, null)", "tts?.speakWithVoice(exercise.audioText, useFemaleVoice, null, audioEnabled)")
content = content.replace("tts?.speakWithVoice(exercise.sentence, useFemaleVoice, null)", "tts?.speakWithVoice(exercise.sentence, useFemaleVoice, null, audioEnabled)")
content = content.replace("tts?.speakWithVoice(exercise.phrase, useFemaleVoice, null)", "tts?.speakWithVoice(exercise.phrase, useFemaleVoice, null, audioEnabled)")

with open('app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt', 'w') as f:
    f.write(content)
