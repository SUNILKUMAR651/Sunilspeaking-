import re
path = "app/src/main/java/com/example/ui/screens/InteractivePracticeScreen.kt"
with open(path, 'r') as f: content = f.read()
if "val userProfile by viewModel.userProfile.collectAsState()" not in content:
    content = content.replace("fun InteractivePracticeScreen(\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {", "fun InteractivePracticeScreen(\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {\n    val userProfile by viewModel.userProfile.collectAsState()")
with open(path, 'w') as f: f.write(content)

path = "app/src/main/java/com/example/ui/screens/LeaderboardScreen.kt"
with open(path, 'r') as f: content = f.read()
if "val userProfile by viewModel.userProfile.collectAsState()" not in content:
    content = content.replace("fun LeaderboardScreen(\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {", "fun LeaderboardScreen(\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {\n    val userProfile by viewModel.userProfile.collectAsState()")
content = content.replace("profile.", "userProfile.")
with open(path, 'w') as f: f.write(content)

path = "app/src/main/java/com/example/ui/screens/ProfileScreen.kt"
with open(path, 'r') as f: content = f.read()
content = content.replace("profile.", "userProfile.")
with open(path, 'w') as f: f.write(content)

path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, 'r') as f: content = f.read()
if "val userProfile by viewModel.userProfile.collectAsState()" not in content:
    content = content.replace("fun PracticeRunScreen(lessonId: Int, viewModel: LexiViewModel, onBack: () -> Unit) {", "fun PracticeRunScreen(lessonId: Int, viewModel: LexiViewModel, onBack: () -> Unit) {\n    val userProfile by viewModel.userProfile.collectAsState()")
content = content.replace("profile.", "userProfile.")
content = content.replace("tts?.speakWithVoice(sentence, userProfile.useFemaleVoice)", "tts?.speakWithVoice(sentence, userProfile.useFemaleVoice, null)")
with open(path, 'w') as f: f.write(content)

