import os
import re

directory = "app/src/main/java/com/example/ui/screens/"
files = [
    "AIRoleplayScreen.kt",
    "InteractivePracticeScreen.kt",
    "VocabularyQuizScreen.kt",
    "AICallScreen.kt",
    "VocabularyArrangementScreen.kt",
    "ActiveLessonScreen.kt",
]

for filename in files:
    filepath = os.path.join(directory, filename)
    if not os.path.exists(filepath):
        continue
        
    with open(filepath, 'r') as f:
        content = f.read()

    # Import the extension function
    if "com.example.utils.speakWithVoice" not in content:
        content = content.replace("import android.speech.tts.TextToSpeech", "import android.speech.tts.TextToSpeech\nimport com.example.utils.speakWithVoice")

    # Add profile state if not present and if viewModel is present
    if "val profile by viewModel.userProfile" not in content and "val userProfile by viewModel.userProfile" not in content:
        # We need to find the composable signature that contains LexiViewModel and add it
        # Actually it's easier to just do it manually for files that need it.
        # But let's see. 
        pass

    # We need a robust way to replace the speak calls
    # For `tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)`
    # Or `tts.value?.speak(...)`
    # Or `ttsInstance.speak(...)`
    # Let's replace manually for each because they differ slightly.
    
    with open(filepath, 'w') as f:
        f.write(content)
