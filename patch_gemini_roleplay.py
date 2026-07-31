with open("app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt", "r") as f:
    content = f.read()

bad_history = """val historyParts = newMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""

good_history = """var validMessages = newMessages
                while (validMessages.isNotEmpty() && !validMessages.first().isUser) {
                    validMessages = validMessages.drop(1)
                }
                if (validMessages.isEmpty()) {
                    validMessages = listOf(RoleplayMessage("Hello", true))
                }
                val historyParts = validMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""

content = content.replace(bad_history, good_history)

with open("app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt", "w") as f:
    f.write(content)
