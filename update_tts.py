path = "app/src/main/java/com/example/utils/TtsExtensions.kt"
with open(path, 'r') as f:
    content = f.read()

content = content.replace("utteranceId: String? = null)", "utteranceId: String? = null, audioEnabled: Boolean = true)")
content = content.replace("this?.speak", "if (audioEnabled) this?.speak")

with open(path, 'w') as f:
    f.write(content)
