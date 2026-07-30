import re

path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

import_str = "import com.example.ui.screens.WeakWordsScreen"
new_import_str = """import com.example.ui.screens.WeakWordsScreen
import com.example.ui.screens.BubblePopScreen
import com.example.ui.screens.CrosswordConnectScreen
import com.example.ui.screens.SwipeBattleScreen
import com.example.ui.screens.AudioDictationScreen"""

if "BubblePopScreen" not in content:
    content = content.replace(import_str, new_import_str)

nav_str = """            composable("weak_words") {"""
new_nav_str = """            composable("bubble_pop") {
                BubblePopScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("crossword_connect") {
                CrosswordConnectScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("swipe_battle") {
                SwipeBattleScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("audio_dictation") {
                AudioDictationScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("weak_words") {"""

if "composable(\"bubble_pop\")" not in content:
    content = content.replace(nav_str, new_nav_str)

with open(path, "w") as f:
    f.write(content)
