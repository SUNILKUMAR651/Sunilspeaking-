import re

path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, "r") as f:
    content = f.read()

import_str = "import androidx.compose.material.icons.filled.RecordVoiceOver"
new_import_str = """import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Abc"""
content = content.replace(import_str, new_import_str)

games_block = """                        item {
                            GlassmorphicActionCard(
                                title = "Speaking Practice",
                                subtitle = "50+ scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.width(160.dp)
                            ) { onNavigate("pro_practice") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Weak Words",
                                accentColor = Color(0xFFE040FB),
                                icon = Icons.Filled.TrendingUp
                            ) { onNavigate("weak_words") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Vocab Quiz",
                                accentColor = Color(0xFFFF4081),
                                icon = Icons.Filled.Style
                            ) { onNavigate("vocabulary_quiz") }
                        }"""

new_games_block = """                        item {
                            GlassmorphicActionCard(
                                title = "Swipe Battle",
                                subtitle = "Which is Right?",
                                icon = Icons.Filled.Swipe,
                                accentColor = Color(0xFFE91E63),
                                modifier = Modifier.width(160.dp)
                            ) { onNavigate("swipe_battle") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Bubble Pop",
                                accentColor = Color(0xFFFF512F),
                                icon = Icons.Filled.Games
                            ) { onNavigate("bubble_pop") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Audio Dictation",
                                accentColor = Color(0xFF4776E6),
                                icon = Icons.Filled.Audiotrack
                            ) { onNavigate("audio_dictation") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Crossword",
                                accentColor = Color(0xFF00C6FF),
                                icon = Icons.Filled.Abc
                            ) { onNavigate("crossword_connect") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Weak Words",
                                accentColor = Color(0xFFE040FB),
                                icon = Icons.Filled.TrendingUp
                            ) { onNavigate("weak_words") }
                        }"""

content = content.replace(games_block, new_games_block)

with open(path, "w") as f:
    f.write(content)
