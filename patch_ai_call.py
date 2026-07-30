import re

path = "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
with open(path, "r") as f:
    content = f.read()

sys_prompt = """                            val systemPrompt = "You are an English teacher having a voice call with a student. Keep your answers short, conversational, and helpful (max 2-3 sentences)." """
new_sys_prompt = """                            val systemPrompt = "You are a ${userProfile.targetLanguage} teacher having a voice call with a student whose native language is ${userProfile.nativeLanguage}. Keep your answers short, conversational, and helpful (max 2-3 sentences)." """
content = content.replace(sys_prompt, new_sys_prompt)

greeting = """                    val greeting = "Hello, I'm your AI English teacher. How can I help you today?" """
new_greeting = """                    val greeting = "Hello, I'm your AI ${userProfile.targetLanguage} teacher. How can I help you today?" """
content = content.replace(greeting, new_greeting)

teacher_text = """                    text = "AI English Teacher","""
new_teacher_text = """                    text = "AI ${userProfile.targetLanguage} Teacher","""
content = content.replace(teacher_text, new_teacher_text)

with open(path, "w") as f:
    f.write(content)
