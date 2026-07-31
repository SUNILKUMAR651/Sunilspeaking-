import re
path = "app/src/main/java/com/example/viewmodel/LexiViewModel.kt"
with open(path, "r") as f:
    content = f.read()

bad = """    fun recordLessonCompletion(xpEarned: Int, skill: String) {
        val currentProfile = _userProfile.value
        val newXp = currentProfile.totalXp + xpEarned
        
        if (xpToAdd > 0) {"""
good = """    fun recordLessonCompletion(xpEarned: Int, skill: String) {
        val currentProfile = _userProfile.value
        val newXp = currentProfile.totalXp + xpEarned
        
        if (xpEarned > 0) {"""

content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
