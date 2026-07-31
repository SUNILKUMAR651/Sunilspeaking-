import re
with open("app/src/main/java/com/example/ui/screens/AICallScreen.kt", "r") as f:
    content = f.read()

bad_history = """var validHistory = conversationHistory
                    while(validHistory.isNotEmpty() && validHistory.first().role != "user") {
                        validHistory = validHistory.drop(1)
                    }
                    val newHistory = validHistory + Content(listOf(Part(userText)), role = "user")"""

good_history = """val rawHistory = conversationHistory + Content(listOf(Part(userText)), role = "user")
                    val collapsedHistory = mutableListOf<Content>()
                    for (msg in rawHistory) {
                        if (collapsedHistory.isEmpty()) {
                            if (msg.role == "user") {
                                collapsedHistory.add(msg)
                            }
                        } else {
                            val last = collapsedHistory.last()
                            if (last.role == msg.role) {
                                val combinedText = (last.parts.firstOrNull()?.text ?: "") + "\\n" + (msg.parts.firstOrNull()?.text ?: "")
                                collapsedHistory[collapsedHistory.size - 1] = Content(listOf(Part(combinedText)), role = last.role)
                            } else {
                                collapsedHistory.add(msg)
                            }
                        }
                    }
                    if (collapsedHistory.isEmpty()) {
                        collapsedHistory.add(Content(listOf(Part("Hello")), role = "user"))
                    }
                    val newHistory = collapsedHistory"""

content = content.replace(bad_history, good_history)
with open("app/src/main/java/com/example/ui/screens/AICallScreen.kt", "w") as f:
    f.write(content)
