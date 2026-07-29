import re

with open('app/src/main/java/com/example/ui/screens/StoryReadingScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("com.example.ui.speaking.levenshteinDistance(e, a)", "levenshteinDistance(e, a)")
content = content.replace("com.example.ui.speaking.ScoreIndicator(score)", "ScoreIndicator(score)")

if "fun levenshteinDistance" not in content:
    content += """

fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
    val len0 = lhs.length + 1
    val len1 = rhs.length + 1
    var cost = IntArray(len0)
    var newcost = IntArray(len0)
    for (i in 0 until len0) cost[i] = i
    for (j in 1 until len1) {
        newcost[0] = j
        for (i in 1 until len0) {
            val match = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
            val cost_replace = cost[i - 1] + match
            val cost_insert = cost[i] + 1
            val cost_delete = newcost[i - 1] + 1
            newcost[i] = minOf(minOf(cost_insert, cost_delete), cost_replace)
        }
        val swap = cost; cost = newcost; newcost = swap
    }
    return cost[len0 - 1]
}

@Composable
fun ScoreIndicator(score: Float) {
    androidx.compose.material3.Text("Score: ${score.toInt()}%", color = androidx.compose.ui.graphics.Color.White)
}
"""

with open('app/src/main/java/com/example/ui/screens/StoryReadingScreen.kt', 'w') as f:
    f.write(content)
