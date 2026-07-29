import os, re
path = "app/src/main/java/com/example/ui/speaking/SpeakingEvaluationScreen.kt"
if os.path.exists(path):
    with open(path, 'r') as f:
        content = f.read()

    if "com.example.utils.speakWithVoice" not in content:
        content = content.replace("import android.speech.tts.TextToSpeech", "import android.speech.tts.TextToSpeech\nimport com.example.utils.speakWithVoice")

    if "val userProfile by viewModel.userProfile" not in content and "val profile by viewModel.userProfile" not in content:
        content = re.sub(r'viewModel:\s*LexiViewModel[^\)]*\)\s*\{', r'\g<0>\n    val userProfile by viewModel.userProfile.collectAsState()', content, count=1)
        
    content = re.sub(r'tts\?\.speak\(([^,]+),\s*TextToSpeech\.QUEUE_FLUSH,\s*null,\s*(null|"[^"]*"|utteranceId)\)', r'tts?.speakWithVoice(\1, userProfile.useFemaleVoice, \2)', content)

    with open(path, 'w') as f:
        f.write(content)
