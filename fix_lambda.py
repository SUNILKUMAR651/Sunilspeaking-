import re
path = "app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt"
with open(path, 'r') as f:
    content = f.read()

content = re.sub(r'fun ([A-Za-z]+View)\(([^,]+),\s*([a-zA-Z]+):\s*\(\)\s*->\s*Unit,\s*useFemaleVoice:\s*Boolean\)\s*\{', r'fun \1(\2, useFemaleVoice: Boolean, \3: () -> Unit) {', content)

with open(path, 'w') as f:
    f.write(content)
