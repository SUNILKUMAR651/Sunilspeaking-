import re

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "r") as f:
    content = f.read()

old_top_bar = """                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$currentSentenceIndex", fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6), fontSize = 18.sp)
                    Text("DONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("0%", fontWeight = FontWeight.Bold, color = Color(0xFFFF9600), fontSize = 18.sp)
                    Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9600))
                }"""

new_top_bar = """                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$currentSentenceIndex", fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6), fontSize = 18.sp)
                    Text("DONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val targetW = sentences[currentSentenceIndex].replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                    val recW = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                    val currentScore = if (recognizedText.isEmpty() || targetW.isEmpty()) 0 else {
                        val matches = recW.count { targetW.contains(it) }
                        (matches.toFloat() / targetW.size * 100).toInt().coerceIn(0, 100)
                    }
                    val scoreColor = if (currentScore > 80) Color(0xFF58CC02) else if (currentScore > 40) Color(0xFFFF9600) else if (currentScore > 0) Color(0xFFFF4B4B) else Color(0xFF1CB0F6)
                    Text("${currentScore}%", fontWeight = FontWeight.Bold, color = scoreColor, fontSize = 18.sp)
                    Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = scoreColor)
                }"""

content = content.replace(old_top_bar, new_top_bar)

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "w") as f:
    f.write(content)
