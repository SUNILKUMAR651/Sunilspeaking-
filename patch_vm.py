import re
path = "app/src/main/java/com/example/viewmodel/LexiViewModel.kt"
with open(path, "r") as f:
    content = f.read()

state_block = """    private val _userProfile = MutableStateFlow(com.example.data.UserProfile())
    val userProfile: StateFlow<com.example.data.UserProfile> = _userProfile.asStateFlow()"""
new_state = """    private val _userProfile = MutableStateFlow(com.example.data.UserProfile())
    val userProfile: StateFlow<com.example.data.UserProfile> = _userProfile.asStateFlow()
    
    private val _showGoalAnimation = MutableStateFlow(false)
    val showGoalAnimation: StateFlow<Boolean> = _showGoalAnimation.asStateFlow()
    
    fun onGoalAnimationShown() {
        _showGoalAnimation.value = false
    }"""
content = content.replace(state_block, new_state)

record_block = """        val newLevel = when {"""
new_record = """        if (xpEarned > 0) {
            _showGoalAnimation.value = true
        }
        val newLevel = when {"""
content = content.replace(record_block, new_record)

with open(path, "w") as f:
    f.write(content)
