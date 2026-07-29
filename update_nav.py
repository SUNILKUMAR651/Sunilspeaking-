import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Make "AI Teacher" -> "AI"
content = content.replace('label = { Text("AI Teacher") }', 'label = { Text("AI", maxLines = 1) }')
content = content.replace('label = { Text("Home") }', 'label = { Text("Home", maxLines = 1) }')
content = content.replace('label = { Text("Learn") }', 'label = { Text("Learn", maxLines = 1) }')
content = content.replace('label = { Text("Practice") }', 'label = { Text("Practice", maxLines = 1) }')
content = content.replace('label = { Text("Profile") }', 'label = { Text("Profile", maxLines = 1) }')


# Let's write it back
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
