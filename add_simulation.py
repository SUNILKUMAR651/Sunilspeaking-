import re

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "r") as f:
    content = f.read()

# Add a simulation button next to the retry button
old_retry = """                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFE5E5),
                        modifier = Modifier.size(56.dp).clickable { recognizedText = "" }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry", tint = Color(0xFFFF4B4B))
                            Text("RETRY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4B4B))
                        }
                    }"""

new_retry = """                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFE5E5),
                        modifier = Modifier.size(56.dp).clickable { recognizedText = "" }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry", tint = Color(0xFFFF4B4B))
                            Text("RETRY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4B4B))
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Simulation Button for Emulator Testing
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF0F5FF),
                        modifier = Modifier.size(56.dp).clickable {
                            // Simulate voice input (perfect, partial, wrong)
                            val target = sentences[currentSentenceIndex]
                            val random = (0..2).random()
                            recognizedText = when (random) {
                                0 -> target // Perfect
                                1 -> target.split(" ").shuffled().take(target.split(" ").size / 2 + 1).joinToString(" ") + " incorrect word" // Partial
                                else -> "I am saying something completely wrong" // Wrong
                            }
                        }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(androidx.compose.material.icons.filled.AutoAwesome, contentDescription = "Simulate", tint = Color(0xFF1CB0F6))
                            Text("SIM", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1CB0F6))
                        }
                    }"""

# Need to import AutoAwesome
if "androidx.compose.material.icons.filled.AutoAwesome" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Refresh", "import androidx.compose.material.icons.filled.Refresh\nimport androidx.compose.material.icons.filled.AutoAwesome")

if old_retry in content:
    content = content.replace(old_retry, new_retry)
else:
    print("Could not find retry block")

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "w") as f:
    f.write(content)
