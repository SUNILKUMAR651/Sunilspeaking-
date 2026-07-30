import re
path = "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
with open(path, "r") as f:
    content = f.read()

fallback_str = """val fallbackResponse = "I seem to be having a little trouble connecting to my knowledge base right now. Let's keep practicing our ${userProfile.targetLanguage}! What else would you like to talk about?\""""

new_fallback = """val fallbackResponse = "I seem to be having a little trouble connecting to my knowledge base right now. Let's keep practicing our ${userProfile.targetLanguage}! (Error: ${e.message})" """

if fallback_str in content:
    content = content.replace(fallback_str, new_fallback)

with open(path, "w") as f:
    f.write(content)
