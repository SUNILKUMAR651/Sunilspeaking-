import os
import re

screens = [
    "AIRoleplayScreen.kt",
    "InteractivePracticeScreen.kt",
    "VocabularyQuizScreen.kt",
    "AICallScreen.kt",
    "VocabularyArrangementScreen.kt",
    "ActiveLessonScreen.kt",
]

dir_path = "app/src/main/java/com/example/ui/screens/"
for s in screens:
    path = os.path.join(dir_path, s)
    if not os.path.exists(path): continue
    with open(path, 'r') as f:
        content = f.read()

    # Collect profile
    if "val profile by viewModel.userProfile.collectAsState()" not in content and "val userProfile by viewModel.userProfile.collectAsState()" not in content:
        # insert at the top of the composable that has viewModel: LexiViewModel
        content = re.sub(r'viewModel:\s*LexiViewModel[^\)]*\)\s*\{', r'\g<0>\n    val userProfile by viewModel.userProfile.collectAsState()', content, count=1)
        
    # Now replace tts calls
    # Usually `tts?.speak(foo, TextToSpeech.QUEUE_FLUSH, null, null)`
    # Sometimes `ttsInstance.speak(cleanResponse, TextToSpeech.QUEUE_FLUSH, null, utteranceId)`
    
    # 1. tts?.speak(...)
    content = re.sub(r'tts\?\.speak\(([^,]+),\s*TextToSpeech\.QUEUE_FLUSH,\s*null,\s*(null|"[^"]*"|utteranceId)\)', r'tts?.speakWithVoice(\1, userProfile.useFemaleVoice, \2)', content)

    # 2. tts.value?.speak(...)
    content = re.sub(r'tts\.value\?\.speak\(([^,]+),\s*TextToSpeech\.QUEUE_FLUSH,\s*null,\s*(null|"[^"]*"|utteranceId)\)', r'tts.value?.speakWithVoice(\1, userProfile.useFemaleVoice, \2)', content)

    # 3. ttsInstance.speak(...)
    content = re.sub(r'ttsInstance\.speak\(([^,]+),\s*TextToSpeech\.QUEUE_FLUSH,\s*null,\s*(null|"[^"]*"|utteranceId)\)', r'ttsInstance.speakWithVoice(\1, userProfile.useFemaleVoice, \2)', content)

    with open(path, 'w') as f:
        f.write(content)
