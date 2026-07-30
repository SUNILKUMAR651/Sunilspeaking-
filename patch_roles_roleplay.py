import re
path = "app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt"
with open(path, "r") as f:
    content = f.read()

bad_history = """val historyParts = newMessages.map { Content(listOf(Part(if(it.isUser) "User: ${it.text}" else "AI: ${it.text}"))) }"""
good_history = """val historyParts = newMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }"""
content = content.replace(bad_history, good_history)

bad_clean = """val cleanResponse = aiResponse.replace(Regex("^(AI: )"), "")"""
good_clean = """val cleanResponse = aiResponse"""
content = content.replace(bad_clean, good_clean)

with open(path, "w") as f:
    f.write(content)
