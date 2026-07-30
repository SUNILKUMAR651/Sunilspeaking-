import re

path = "app/src/main/java/com/example/ui/screens/LearningPathScreen.kt"
with open(path, "r") as f:
    content = f.read()

# I will find the first PathLesson block and remove it
content = re.sub(r'data class PathLesson\([^)]+\)', '', content, count=1)

with open(path, "w") as f:
    f.write(content)
