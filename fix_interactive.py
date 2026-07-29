import re
path = "app/src/main/java/com/example/ui/screens/InteractivePracticeScreen.kt"
with open(path, 'r') as f: content = f.read()
content = content.replace("fun InteractivePracticeScreen(\n    title: String,\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {", "fun InteractivePracticeScreen(\n    title: String,\n    viewModel: LexiViewModel,\n    onBack: () -> Unit\n) {\n    val userProfile by viewModel.userProfile.collectAsState()")
with open(path, 'w') as f: f.write(content)
