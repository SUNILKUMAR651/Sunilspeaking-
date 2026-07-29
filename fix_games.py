path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, 'r') as f:
    content = f.read()

import re

old_games = """                        item {
                            GlassmorphicActionCard(
                                title = "Speaking Practice",
                                subtitle = "50+ scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.width(160.dp)
                            ) { onNavigate("pro_practice") }
                        }"""

new_games = """                        item {
                            GlassmorphicActionCard(
                                title = "Speaking",
                                subtitle = "50+ scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.width(160.dp)
                            ) { onNavigate("pro_practice") }
                        }"""

content = content.replace(old_games, new_games)

with open(path, 'w') as f:
    f.write(content)
