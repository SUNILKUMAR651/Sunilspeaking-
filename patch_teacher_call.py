import re
import os

path = "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
with open(path, "r") as f:
    content = f.read()

content = re.sub(r'val systemPrompt = "[^"]+"', 'val systemPrompt = "You are a ${userProfile.targetLanguage} teacher having a voice call with a student whose native language is ${userProfile.nativeLanguage}. Keep your answers short, conversational, and helpful (max 2-3 sentences)."', content, count=1)
content = re.sub(r'val greeting = "[^"]+"', 'val greeting = "Hello, I\'m your AI ${userProfile.targetLanguage} teacher. How can I help you today?"', content, count=1)
content = content.replace('"AI English Teacher"', '"AI ${userProfile.targetLanguage} Teacher"')
with open(path, "w") as f:
    f.write(content)

path2 = "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt"
with open(path2, "r") as f:
    content2 = f.read()
content2 = re.sub(r'val systemPrompt = "[^"]+"', 'val systemPrompt = "You are an expert ${userProfile.targetLanguage} language teacher. The user\'s native language is ${userProfile.nativeLanguage}. Help the user improve their grammar, vocabulary, and speaking skills. Correct mistakes gently, provide explanations when asked, and be encouraging. If the user makes a grammar mistake, provide a small \'💡 Tip:\' at the end of your response."', content2, count=1)
content2 = re.sub(r'val fallbackResponse = "[^"]+"', 'val fallbackResponse = "I seem to be having a little trouble connecting to my knowledge base right now. Let\'s keep practicing our ${userProfile.targetLanguage}! What else would you like to talk about?"', content2, count=1)
with open(path2, "w") as f:
    f.write(content2)

