with open("app/src/main/java/com/example/ui/screens/AICallScreen.kt", "r") as f:
    content = f.read()

bad_history = """val newHistory = conversationHistory + Content(listOf(Part(userText)), role = "user")"""

good_history = """var validHistory = conversationHistory
                    while(validHistory.isNotEmpty() && validHistory.first().role != "user") {
                        validHistory = validHistory.drop(1)
                    }
                    val newHistory = validHistory + Content(listOf(Part(userText)), role = "user")"""

content = content.replace(bad_history, good_history)

with open("app/src/main/java/com/example/ui/screens/AICallScreen.kt", "w") as f:
    f.write(content)
