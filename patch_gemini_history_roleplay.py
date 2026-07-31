import re
with open("app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt", "r") as f:
    content = f.read()

bad_history = """var validMessages = newMessages
                while (validMessages.isNotEmpty() && !validMessages.first().isUser) {
                    validMessages = validMessages.drop(1)
                }
                if (validMessages.isEmpty()) {
                    validMessages = listOf(RoleplayMessage("Hello", true))
                }
                val historyParts = validMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""

good_history = """val collapsedMessages = mutableListOf<RoleplayMessage>()
                for (msg in newMessages) {
                    if (collapsedMessages.isEmpty()) {
                        if (msg.isUser) {
                            collapsedMessages.add(msg)
                        }
                    } else {
                        val last = collapsedMessages.last()
                        if (last.isUser == msg.isUser) {
                            collapsedMessages[collapsedMessages.size - 1] = RoleplayMessage(last.text + "\\n" + msg.text, last.isUser)
                        } else {
                            collapsedMessages.add(msg)
                        }
                    }
                }
                if (collapsedMessages.isEmpty()) {
                    collapsedMessages.add(RoleplayMessage("Hello", true))
                }
                val historyParts = collapsedMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""

content = content.replace(bad_history, good_history)
with open("app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt", "w") as f:
    f.write(content)
