import re
path = "app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt"
with open(path, 'r') as f:
    content = f.read()

# Fix the method signatures
content = re.sub(r'fun ([A-Za-z]+View)\(([^,]+),\s*([a-zA-Z]+):\s*\(\,\s*useFemaleVoice\s*=\s*userProfile\.useFemaleVoice\)\s*->\s*Unit\)\s*\{', r'fun \1(\2, \3: () -> Unit, useFemaleVoice: Boolean) {', content)

with open(path, 'w') as f:
    f.write(content)
