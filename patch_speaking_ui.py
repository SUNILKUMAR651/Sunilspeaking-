import re

path = "app/src/main/java/com/example/ui/speaking/SpeakingEvaluationScreen.kt"
with open(path, "r") as f:
    content = f.read()

old_result = """        } else if (showResult) {
            Text(
                text = recognizedText,
                fontSize = 18.sp,
                color = if (isSuccess) Color(0xFF58CC02) else Color(0xFFFF4B4B),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
        }"""

new_result = """        } else if (showResult) {
            Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (isSuccess) "Excellent!" else "Let's try again", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isSuccess) Color(0xFF58CC02) else Color(0xFFFF4B4B))
                Spacer(modifier = Modifier.height(8.dp))
                
                // Show words with color coding
                val targetWords = currentSentence.split(" ")
                val recognizedWordsListChips = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    targetWords.forEach { word ->
                        val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                        val isSpoken = recognizedWordsListChips.contains(cleanWord)
                        
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpoken) Color(0xFF58CC02) else Color(0xFFFF4B4B)),
                            color = if (isSpoken) Color(0xFFD7FFB8) else Color(0xFFFFE5E5)
                        ) {
                            Text(word, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isSpoken) Color(0xFF58CC02) else Color(0xFFFF4B4B))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("You said: $recognizedText", fontSize = 14.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }"""

# add import for FlowRow
if "import androidx.compose.foundation.layout.ExperimentalLayoutApi" not in content:
    content = content.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.layout.ExperimentalLayoutApi\nimport androidx.compose.foundation.layout.FlowRow")

content = content.replace(old_result, new_result)

with open(path, "w") as f:
    f.write(content)
