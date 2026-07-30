import re

path = "app/src/main/java/com/example/ui/screens/SidebarMenu.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace('title = "English Level",', 'title = "${userProfile.targetLanguage} Level",')
with open(path, "w") as f:
    f.write(content)

path2 = "app/src/main/java/com/example/ui/screens/SettingsScreen.kt"
with open(path2, "r") as f:
    content2 = f.read()
content2 = content2.replace('title = "English Level",', 'title = "${userProfile.targetLanguage} Level",')
with open(path2, "w") as f:
    f.write(content2)

