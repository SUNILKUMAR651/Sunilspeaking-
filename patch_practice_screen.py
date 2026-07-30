import re

path = "app/src/main/java/com/example/ui/screens/PracticeScreen.kt"
with open(path, "r") as f:
    content = f.read()

import_str = "import androidx.compose.material.icons.filled.SupportAgent"
new_import_str = """import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Abc"""
content = content.replace(import_str, new_import_str)

modules_str = """        PracticeModule("AI Roleplay", "Real-life scenarios", Icons.Filled.SupportAgent, "ai_roleplay", Color(0xFFE91E63), Color(0xFF9C27B0)),
        PracticeModule("Mock Interview", "AI HR simulation", Icons.Filled.Headset, "mock_interview", Color(0xFF00C6FF), Color(0xFF0072FF)),
        PracticeModule("Speaking Coach", "Pronunciation & fluency", Icons.Filled.RecordVoiceOver, "speaking_practice", Color(0xFFFF512F), Color(0xFFDD2476)),
        PracticeModule("Smart Flashcards", "Spaced repetition", Icons.Filled.Style, "smart_flashcards", Color(0xFF4776E6), Color(0xFF8E54E9)),
        PracticeModule("Word Battle", "Compete globally", Icons.Filled.SportsEsports, "word_battle", Color(0xFFF12711), Color(0xFFF5AF19)),
        PracticeModule("Vocab Quiz", "Test your memory", Icons.Filled.Spellcheck, "vocabulary_quiz", Color(0xFF56AB2F), Color(0xFFA8E063)),
        PracticeModule("Grammar Rule", "Sentence mastery", Icons.AutoMirrored.Filled.Rule, "grammar_challenge", Color(0xFF11998E), Color(0xFF38EF7D))"""

new_modules_str = """        PracticeModule("Bubble Pop", "Speed Tapper", Icons.Filled.Games, "bubble_pop", Color(0xFFFF512F), Color(0xFFDD2476)),
        PracticeModule("Crossword Connect", "Wordscapes Style", Icons.Filled.Abc, "crossword_connect", Color(0xFF00C6FF), Color(0xFF0072FF)),
        PracticeModule("Swipe Battle", "Which is Right?", Icons.Filled.Swipe, "swipe_battle", Color(0xFFE91E63), Color(0xFF9C27B0)),
        PracticeModule("Audio Dictation", "Listen and write", Icons.Filled.Audiotrack, "audio_dictation", Color(0xFF4776E6), Color(0xFF8E54E9)),
        PracticeModule("Word Battle", "Compete globally", Icons.Filled.SportsEsports, "word_battle", Color(0xFFF12711), Color(0xFFF5AF19)),
        PracticeModule("Vocab Quiz", "Test your memory", Icons.Filled.Spellcheck, "vocabulary_quiz", Color(0xFF56AB2F), Color(0xFFA8E063)),
        PracticeModule("Grammar Rule", "Sentence mastery", Icons.AutoMirrored.Filled.Rule, "grammar_challenge", Color(0xFF11998E), Color(0xFF38EF7D))"""

content = content.replace(modules_str, new_modules_str)

with open(path, "w") as f:
    f.write(content)
