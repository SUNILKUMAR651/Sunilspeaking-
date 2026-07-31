import re
path = "app/src/main/java/com/example/data/UserProfile.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace('var notificationsEnabled: Boolean = true,', 'var notificationsEnabled: Boolean = true,\n    var reminderHour: Int = 18,\n    var reminderMinute: Int = 0,')

with open(path, "w") as f:
    f.write(content)
