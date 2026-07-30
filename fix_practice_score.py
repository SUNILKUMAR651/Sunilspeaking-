import re

path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, "r") as f:
    content = f.read()

# Replace the SCORE block inside PracticeMainPanel
old_score = """                        Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("0%", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1CB0F6))"""

new_score = """                        Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        val targetWords = sentence.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        val recognizedWordsList = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        val score = if (recognizedText.isEmpty() || targetWords.isEmpty()) 0 else {
                            val matches = recognizedWordsList.count { targetWords.contains(it) }
                            (matches.toFloat() / targetWords.size * 100).toInt().coerceIn(0, 100)
                        }
                        Text("${score}%", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1CB0F6))"""

content = content.replace(old_score, new_score)

with open(path, "w") as f:
    f.write(content)
