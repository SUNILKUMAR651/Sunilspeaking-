import re

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "r") as f:
    content = f.read()

imports = """import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
"""

content = content.replace("import androidx.compose.ui.text.font.FontWeight", imports + "import androidx.compose.ui.text.font.FontWeight")

old_chips = """            FlowRow(
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

new_chips = """            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val recognizedWordsListChips = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                sentence.split(" ").forEach { word ->
                    val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                    val isSpoken = recognizedWordsListChips.contains(cleanWord)
                    val isWrong = !isSpoken && !isRecording && recognizedText.isNotEmpty()
                    
                    val borderColor = if (isSpoken) Color(0xFF58CC02) else if (isWrong) Color(0xFFFF4B4B) else Color(0xFFE5E5E5)
                    val bgColor = if (isSpoken) Color(0xFFD7FFB8) else if (isWrong) Color(0xFFFFE5E5) else Color.White
                    val textColor = if (isSpoken) Color(0xFF58CC02) else if (isWrong) Color(0xFFFF4B4B) else Color(0xFF4B4B4B)
                    
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        color = bgColor
                    ) {
                        Text(word, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }
                }
            }"""

content = content.replace(old_chips, new_chips)


old_boxes = """            // You Said / Score box
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
                    modifier = Modifier.weight(1f).height(100.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("YOU SAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(if (recognizedText.isEmpty()) "Waiting for your voice..." else recognizedText, color = Color.DarkGray, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
                    modifier = Modifier.width(100.dp).height(100.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        val targetWords = sentence.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        val recognizedWordsList = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        val score = if (recognizedText.isEmpty() || targetWords.isEmpty()) 0 else {
                            val matches = recognizedWordsList.count { targetWords.contains(it) }
                            (matches.toFloat() / targetWords.size * 100).toInt().coerceIn(0, 100)
                        }
                        Text("${score}%", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1CB0F6))
                    }
                }
            }"""

new_boxes = """            // You Said / Score box
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C323A)),
                    color = Color(0xFF181C20),
                    modifier = Modifier.weight(1f).height(100.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("YOU SAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val targetWords = sentence.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        
                        if (recognizedText.isEmpty()) {
                            Text("Waiting for your voice...", color = Color.DarkGray, fontSize = 16.sp)
                        } else {
                            val annotatedString = buildAnnotatedString {
                                recognizedText.split(" ").forEach { word ->
                                    val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                                    val isCorrect = targetWords.contains(cleanWord)
                                    withStyle(style = SpanStyle(color = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B))) {
                                        append("$word ")
                                    }
                                }
                            }
                            Text(text = annotatedString, fontSize = 16.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C323A)),
                    color = Color(0xFF181C20),
                    modifier = Modifier.width(100.dp).height(100.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        val targetWords = sentence.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        val recognizedWordsList = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        val score = if (recognizedText.isEmpty() || targetWords.isEmpty()) 0 else {
                            val matches = recognizedWordsList.count { targetWords.contains(it) }
                            (matches.toFloat() / targetWords.size * 100).toInt().coerceIn(0, 100)
                        }
                        val scoreColor = if (score > 80) Color(0xFF58CC02) else if (score > 40) Color(0xFFFF9600) else if (score > 0) Color(0xFFFF4B4B) else Color(0xFF1CB0F6)
                        Text("${score}%", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = scoreColor)
                    }
                }
            }"""

content = content.replace(old_boxes, new_boxes)

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "w") as f:
    f.write(content)
