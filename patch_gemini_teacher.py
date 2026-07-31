with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "r") as f:
    content = f.read()

bad_history = """val historyParts = newMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""

good_history = """// Ensure the conversation starts with a user message to satisfy Gemini API requirements
                var validMessages = newMessages
                while (validMessages.isNotEmpty() && !validMessages.first().isUser) {
                    validMessages = validMessages.drop(1)
                }
                if (validMessages.isEmpty()) {
                    validMessages = listOf(TeacherMessage("Hello", true))
                }
                
                val historyParts = validMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""

content = content.replace(bad_history, good_history)

with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "w") as f:
    f.write(content)
