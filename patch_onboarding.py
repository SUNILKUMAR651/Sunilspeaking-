import re

path = "app/src/main/java/com/example/ui/screens/OnboardingScreen.kt"
with open(path, "r") as f:
    content = f.read()

languages_list = """val languages = listOf("Hindi", "Spanish", "French", "German", "Mandarin", "Arabic", "Portuguese", "Russian", "Japanese", "Korean", "Italian", "Turkish", "Vietnamese", "Polish", "Dutch", "Thai", "Indonesian", "Malay", "Bengali")"""
new_languages_list = """val languages = listOf("English", "Hindi", "Spanish", "French", "German", "Mandarin", "Arabic", "Portuguese", "Russian", "Japanese", "Korean", "Italian", "Turkish", "Vietnamese", "Polish", "Dutch", "Thai", "Indonesian", "Malay", "Bengali")"""
content = content.replace(languages_list, new_languages_list)

with open(path, "w") as f:
    f.write(content)
