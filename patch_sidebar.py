import re

path = "app/src/main/java/com/example/ui/screens/SidebarMenu.kt"
with open(path, "r") as f:
    content = f.read()

old_weak_words = """                SidebarItem(
                    icon = Icons.Filled.Whatshot,
                    iconBgColor = Color(0xFFFF5252),
                    title = "Weak Words Review",
                    subtitle = "Strengthen your vocabulary",
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigate("weak_words")
                    }
                )"""

new_weak_words = """                SidebarItem(
                    icon = Icons.Filled.Whatshot,
                    iconBgColor = Color(0xFFFF5252),
                    title = "Weak Words Review",
                    subtitle = "Strengthen your vocabulary",
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigate("weak_words")
                    }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarItem(
                    icon = Icons.Filled.History,
                    iconBgColor = Color(0xFF1CB0F6),
                    title = "Pronunciation History",
                    subtitle = "Listen to your recordings",
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigate("pronunciation_history")
                    }
                )"""

if "import androidx.compose.material.icons.filled.History" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Whatshot", "import androidx.compose.material.icons.filled.Whatshot\nimport androidx.compose.material.icons.filled.History")

content = content.replace(old_weak_words, new_weak_words)

with open(path, "w") as f:
    f.write(content)
