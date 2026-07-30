import re

path = "app/src/main/java/com/example/ui/speaking/SpeakingEvaluationScreen.kt"
with open(path, "r") as f:
    content = f.read()

old_logic = """                    showResult = true
                    // simple check for demo
                    isSuccess = recognizedText.lowercase().contains("hello") || recognizedText.length > 5"""

new_logic = """                    showResult = true
                    val targetWords = currentSentence.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                    val recognizedWordsList = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                    
                    val matches = recognizedWordsList.count { targetWords.contains(it) }
                    val score = if (targetWords.isEmpty()) 0 else (matches.toFloat() / targetWords.size * 100).toInt().coerceIn(0, 100)
                    
                    isSuccess = score >= 70"""

content = content.replace(old_logic, new_logic)

with open(path, "w") as f:
    f.write(content)
