import re

# 1. PracticeRunScreen
path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, 'r') as f: content = f.read()
content = content.replace("PracticeMainPanel(sentences[currentSentenceIndex], currentSentenceIndex + 1, sentences.size)", "PracticeMainPanel(sentences[currentSentenceIndex], currentSentenceIndex + 1, sentences.size, tts, userProfile.useFemaleVoice)")
content = content.replace("PracticeMainPanel(sentences[currentSentenceIndex], currentSentenceIndex + 1, sentences.size, modifier = Modifier.weight(1f))", "PracticeMainPanel(sentences[currentSentenceIndex], currentSentenceIndex + 1, sentences.size, tts, userProfile.useFemaleVoice, modifier = Modifier.weight(1f))")
with open(path, 'w') as f: f.write(content)

# 2. ProfileScreen
path = "app/src/main/java/com/example/ui/screens/ProfileScreen.kt"
with open(path, 'r') as f: content = f.read()
content = content.replace("profile.", "userProfile.")
with open(path, 'w') as f: f.write(content)

# 3. InteractivePracticeScreen
path = "app/src/main/java/com/example/ui/screens/InteractivePracticeScreen.kt"
with open(path, 'r') as f: content = f.read()
if "val userProfile by viewModel.userProfile.collectAsState()" not in content:
    content = content.replace("fun InteractivePracticeScreen(\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {", "fun InteractivePracticeScreen(\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {\n    val userProfile by viewModel.userProfile.collectAsState()")
with open(path, 'w') as f: f.write(content)

# 4. LeaderboardScreen
path = "app/src/main/java/com/example/ui/screens/LeaderboardScreen.kt"
with open(path, 'r') as f: content = f.read()
if "val userProfile by viewModel.userProfile.collectAsState()" not in content:
    content = content.replace("fun LeaderboardScreen(viewModel: LexiViewModel, onBack: () -> Unit) {", "fun LeaderboardScreen(viewModel: LexiViewModel, onBack: () -> Unit) {\n    val userProfile by viewModel.userProfile.collectAsState()")
    content = content.replace("fun LeaderboardScreen(\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {", "fun LeaderboardScreen(\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {\n    val userProfile by viewModel.userProfile.collectAsState()")
content = content.replace("profile.", "userProfile.")
with open(path, 'w') as f: f.write(content)

