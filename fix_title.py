path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, 'r') as f:
    content = f.read()

import re

old_title = """    val title = "${userProfile.name}'s practice run\""""
new_title = """    val displayName = userProfile.name.ifBlank { "User" }.split(" ").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "User"
    val title = "${displayName}'s Practice Run\""""

content = content.replace(old_title, new_title)

old_text = """                    Text(
                        text = title.uppercase(),
                        color = Color.White,"""
new_text = """                    Text(
                        text = title.uppercase(),
                        color = Color.White,"""

# Actually let's make it look more professional, maybe not uppercase but just title case
old_surface = """                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF6B4EE6)
                ) {
                    Text(
                        text = title.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }"""
new_surface = """                Text(
                    text = title,
                    color = Color(0xFF4B4B4B),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )"""

content = content.replace(old_surface, new_surface)

with open(path, 'w') as f:
    f.write(content)
