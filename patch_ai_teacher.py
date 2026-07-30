import re

path = "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt"
with open(path, "r") as f:
    content = f.read()

greeting = """messages = listOf(TeacherMessage("Hello! I am your AI English Teacher. How can I help you practice today? Feel free to ask me questions, or let me know what you want to learn.", false))"""
new_greeting = """messages = listOf(TeacherMessage("Hello! I am your AI ${userProfile.targetLanguage} Teacher. How can I help you practice today? Feel free to ask me questions, or let me know what you want to learn.", false))"""
content = content.replace(greeting, new_greeting)

sys_prompt = """                val systemPrompt = "You are an expert English language teacher. Help the user improve their grammar, vocabulary, and speaking skills. Correct mistakes gently, provide explanations when asked, and be encouraging. If the user makes a grammar mistake, provide a small '💡 Tip:' at the end of your response." """
new_sys_prompt = """                val systemPrompt = "You are an expert ${userProfile.targetLanguage} language teacher. The user's native language is ${userProfile.nativeLanguage}. Help the user improve their grammar, vocabulary, and speaking skills. Correct mistakes gently, provide explanations when asked, and be encouraging. If the user makes a grammar mistake, provide a small '💡 Tip:' at the end of your response." """
content = content.replace(sys_prompt, new_sys_prompt)

fallback = """                val fallbackResponse = "I seem to be having a little trouble connecting to my knowledge base right now. Let's keep practicing our English! What else would you like to talk about?" """
new_fallback = """                val fallbackResponse = "I seem to be having a little trouble connecting to my knowledge base right now. Let's keep practicing our ${userProfile.targetLanguage}! What else would you like to talk about?" """
content = content.replace(fallback, new_fallback)

with open(path, "w") as f:
    f.write(content)
