import re
path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

import_str = "import com.example.ui.screens.AudioDictationScreen"
new_import_str = """import com.example.ui.screens.AudioDictationScreen
import com.example.ui.screens.WordWheelScreen"""
content = content.replace(import_str, new_import_str)

nav_str = """            composable("audio_dictation") {"""
new_nav_str = """            composable("word_wheel") {
                WordWheelScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("audio_dictation") {"""
content = content.replace(nav_str, new_nav_str)

with open(path, "w") as f:
    f.write(content)
