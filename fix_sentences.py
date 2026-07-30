path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, 'r') as f:
    content = f.read()

content = content.replace("Hello, my name is ${userProfile.name}.", "Hello, my name is ${displayName}.")

with open(path, 'w') as f:
    f.write(content)
