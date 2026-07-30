import re

path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace('"Business English"', '"Business ${userProfile.targetLanguage}"')
content = content.replace('"Medical English"', '"Medical ${userProfile.targetLanguage}"')
content = content.replace('"Movie English"', '"Movie ${userProfile.targetLanguage}"')

with open(path, "w") as f:
    f.write(content)
