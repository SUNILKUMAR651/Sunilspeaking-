path = "app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt"
with open(path, 'r') as f:
    content = f.read()

import re
views = ["NewWordView", "ListeningView", "SpeakingSentenceView", "TranslationView", "ArrangeWordsView", "FillInTheBlanksView", "MatchPairsView", "PhraseNarrationView"]

for view in views:
    content = re.sub(fr'fun {view}\(([^,]+),\s*useFemaleVoice:\s*Boolean,\s*([^:]+):\s*\(\)\s*->\s*Unit\)\s*{{', 
                     fr'fun {view}(\1, useFemaleVoice: Boolean, audioEnabled: Boolean = true, \2: () -> Unit) {{', content)

with open(path, 'w') as f:
    f.write(content)
