import re

path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, "r") as f:
    content = f.read()

old_chips = """            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sentence.split(" ").forEach { word ->
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
                        color = Color.White
                    ) {
                        Text(word, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B4B4B))
                    }
                }
            }"""

new_chips = """            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val recognizedWordsListChips = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                sentence.split(" ").forEach { word ->
                    val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                    val isSpoken = recognizedWordsListChips.contains(cleanWord)
                    
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpoken) Color(0xFF58CC02) else Color(0xFFE5E5E5)),
                        color = if (isSpoken) Color(0xFFD7FFB8) else Color.White
                    ) {
                        Text(word, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isSpoken) Color(0xFF58CC02) else Color(0xFF4B4B4B))
                    }
                }
            }"""

content = content.replace(old_chips, new_chips)

with open(path, "w") as f:
    f.write(content)
