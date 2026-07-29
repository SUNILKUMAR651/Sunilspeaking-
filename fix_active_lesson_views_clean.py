path = "app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt"
with open(path, 'r') as f:
    content = f.read()

import re
content = re.sub(r'audioEnabled = userProfile\.audioEnabled(?:, audioEnabled = userProfile\.audioEnabled)+', r'audioEnabled = userProfile.audioEnabled', content)

with open(path, 'w') as f:
    f.write(content)
