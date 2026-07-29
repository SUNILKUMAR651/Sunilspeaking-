import re

with open('app/src/main/java/com/example/ui/screens/StoryReadingScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("com.example.ui.speaking.ScoreIndicator(\"Accuracy\", accuracyScore)", "ScoreIndicator(\"Accuracy\", accuracyScore)")
content = content.replace("com.example.ui.speaking.ScoreIndicator(\"Fluency\", fluencyScore)", "ScoreIndicator(\"Fluency\", fluencyScore)")
content = content.replace("fun levenshteinDistance(", "private fun levenshteinDistance(")

content = content.replace(
"""fun ScoreIndicator(score: Float) {
    androidx.compose.material3.Text("Score: ${score.toInt()}%", color = androidx.compose.ui.graphics.Color.White)
}""",
"""fun ScoreIndicator(label: String, score: Float) {
    androidx.compose.material3.Text("$label: ${score.toInt()}%", color = androidx.compose.ui.graphics.Color.Black)
}""")

with open('app/src/main/java/com/example/ui/screens/StoryReadingScreen.kt', 'w') as f:
    f.write(content)
