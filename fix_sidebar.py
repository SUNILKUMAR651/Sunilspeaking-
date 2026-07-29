path = "app/src/main/java/com/example/ui/screens/SidebarMenu.kt"
with open(path, 'r') as f:
    content = f.read()

old_item = """                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarItem(
                    icon = Icons.Filled.RecordVoiceOver,
                    iconBgColor = Color(0xFFFF9600),
                    title = "Speaking Practice",
                    subtitle = "50+ real-life scenarios",
                    onClick = {
                        onNavigate("pro_practice")
                        onCloseDrawer()
                    }
                )"""

content = content.replace(old_item, "")

with open(path, 'w') as f:
    f.write(content)
