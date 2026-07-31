import os

files = [
    "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt",
    "app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt",
    "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
]

for path in files:
    if os.path.exists(path):
        with open(path, "r") as f:
            content = f.read()
        
        # Replace the error message template
        import re
        content = re.sub(
            r'\(Error: \$\{e\.message\}\)', 
            '', 
            content
        )
        content = re.sub(
            r'\(Error: \$\{e\.message\}\s*\)', 
            '', 
            content
        )
        
        with open(path, "w") as f:
            f.write(content)
