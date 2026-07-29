import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace(
'''                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home", maxLines = 1) }''',
'''                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home", maxLines = 1) },
                    alwaysShowLabel = false''')

content = content.replace(
'''                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = "Learn") },
                    label = { Text("Learn", maxLines = 1) }''',
'''                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = "Learn") },
                    label = { Text("Learn", maxLines = 1) },
                    alwaysShowLabel = false''')

content = content.replace(
'''                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Practice") },
                    label = { Text("Practice", maxLines = 1) }''',
'''                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Practice") },
                    label = { Text("Practice", maxLines = 1) },
                    alwaysShowLabel = false''')

content = content.replace(
'''                    icon = { Icon(Icons.Filled.SmartToy, contentDescription = "AI Teacher") },
                    label = { Text("AI", maxLines = 1) }''',
'''                    icon = { Icon(Icons.Filled.SmartToy, contentDescription = "AI Teacher") },
                    label = { Text("AI", maxLines = 1) },
                    alwaysShowLabel = false''')

content = content.replace(
'''                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile", maxLines = 1) }''',
'''                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile", maxLines = 1) },
                    alwaysShowLabel = false''')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

