import re
import os

files = {
    "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt": [
        (r'Text\("AI Teacher"', r'Text("AI English Teacher"'),
        (r'val history = doc\.get\("messages"\) as\? List<Map<String, Any>>\s+if \(history != null\) \{\s+messages = history\.map \{\s+TeacherMessage\(it\["text"\] as String, it\["isUser"\] as Boolean\)\s+\}', r'val history = doc.get("messages") as? List<Map<String, Any>>\n                    if (history != null) {\n                        messages = history.map {\n                             TeacherMessage(it["text"] as String, it["isUser"] as Boolean)\n                         }.filter { !it.text.contains("Error 404") }')
    ],
    "app/src/main/java/com/example/ui/screens/AICallScreen.kt": [
        (r'Text\(\s*text = "AI \$\{userProfile\.targetLanguage\} Teacher"', r'Text(\n                    text = "AI English Teacher"'),
        (r'Text\("AI Teacher"', r'Text("AI English Teacher"')
    ],
    "app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt": [
        (r'Text\(\s*text = "Roleplay with AI"', r'Text(\n                text = "AI English Teacher"'),
        (r'val history = doc\.get\("messages"\) as\? List<Map<String, Any>>\s+if \(history != null\) \{\s+messages = history\.map \{\s+TeacherMessage\(it\["text"\] as String, it\["isUser"\] as Boolean\)\s+\}', r'val history = doc.get("messages") as? List<Map<String, Any>>\n                    if (history != null) {\n                        messages = history.map {\n                             TeacherMessage(it["text"] as String, it["isUser"] as Boolean)\n                         }.filter { !it.text.contains("Error 404") }')
    ],
    "app/src/main/java/com/example/MainActivity.kt": [
        (r'"AI Teacher"', r'"AI English Teacher"')
    ],
    "app/src/main/java/com/example/ui/screens/PremiumSubscriptionScreen.kt": [
        (r'AI Teacher', r'AI English Teacher')
    ]
}

for path, replacements in files.items():
    if os.path.exists(path):
        with open(path, "r") as f:
            content = f.read()
        
        for bad, good in replacements:
            content = re.sub(bad, good, content)
            
        with open(path, "w") as f:
            f.write(content)
