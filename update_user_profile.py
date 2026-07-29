path = "app/src/main/java/com/example/data/UserProfile.kt"
with open(path, 'r') as f:
    content = f.read()

if "var audioEnabled: Boolean = true" not in content:
    content = content.replace("var useFemaleVoice: Boolean = true", "var useFemaleVoice: Boolean = true,\n    var audioEnabled: Boolean = true")
    with open(path, 'w') as f:
        f.write(content)
