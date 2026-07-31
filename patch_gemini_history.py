import re
with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "r") as f:
    content = f.read()

bad_history = """var validMessages = newMessages
                while (validMessages.isNotEmpty() && !validMessages.first().isUser) {
                    validMessages = validMessages.drop(1)
                }
                if (validMessages.isEmpty()) {
                    validMessages = listOf(TeacherMessage("Hello", true))
                }
                
                val historyParts = validMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""

good_history = """// Gemini API strictly requires alternating roles starting with "user".
                val collapsedMessages = mutableListOf<TeacherMessage>()
                for (msg in newMessages) {
                    if (collapsedMessages.isEmpty()) {
                        if (msg.isUser) {
                            collapsedMessages.add(msg)
                        }
                    } else {
                        val last = collapsedMessages.last()
                        if (last.isUser == msg.isUser) {
                            collapsedMessages[collapsedMessages.size - 1] = TeacherMessage(last.text + "\\n" + msg.text, last.isUser)
                        } else {
                            collapsedMessages.add(msg)
                        }
                    }
                }
                if (collapsedMessages.isEmpty()) {
                    collapsedMessages.add(TeacherMessage("Hello", true))
                }
                val historyParts = collapsedMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""

content = content.replace(bad_history, good_history)
with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "w") as f:
    f.write(content)
