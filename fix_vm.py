import re
path = "app/src/main/java/com/example/viewmodel/LexiViewModel.kt"
with open(path, "r") as f:
    content = f.read()

bad_block = """    fun addXp(xpToAdd: Int) {
        val currentProfile = _userProfile.value
        val newXp = currentProfile.totalXp + xpToAdd
        
        if (xpEarned > 0) {
            _showGoalAnimation.value = true
        }"""

good_block = """    fun addXp(xpToAdd: Int) {
        val currentProfile = _userProfile.value
        val newXp = currentProfile.totalXp + xpToAdd
        
        if (xpToAdd > 0) {
            _showGoalAnimation.value = true
        }"""

content = content.replace(bad_block, good_block)

with open(path, "w") as f:
    f.write(content)
