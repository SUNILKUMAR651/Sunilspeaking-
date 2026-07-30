import re
path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, "r") as f:
    content = f.read()

import_str = "import androidx.compose.material.icons.filled.Games"
new_import_str = """import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.DataExploration"""
content = content.replace(import_str, new_import_str)

games_block = """                        item {
                            GlassmorphicGameCard(
                                title = "Audio Dictation","""

new_games_block = """                        item {
                            GlassmorphicGameCard(
                                title = "Word Wheel",
                                accentColor = Color(0xFF56AB2F),
                                icon = Icons.Filled.DataExploration
                            ) { onNavigate("word_wheel") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Audio Dictation","""
content = content.replace(games_block, new_games_block)

with open(path, "w") as f:
    f.write(content)
