import re
path = "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt"
with open(path, "r") as f:
    content = f.read()
content = content.replace('TeacherMessage("Hello! I am your AI English Teacher.', 'TeacherMessage("Hello! I am your AI ${userProfile.targetLanguage} Teacher.')
with open(path, "w") as f:
    f.write(content)
