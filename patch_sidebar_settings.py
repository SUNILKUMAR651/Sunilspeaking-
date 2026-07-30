import re

# Sidebar
path = "app/src/main/java/com/example/ui/screens/SidebarMenu.kt"
with open(path, "r") as f:
    content = f.read()

native_sidebar = """                SidebarItem(
                    icon = Icons.Filled.Language,
                    title = "Native Language",
                    trailingText = userProfile.nativeLanguage,
                    onClick = {}
                )"""
target_sidebar = """                SidebarItem(
                    icon = Icons.Filled.Language,
                    title = "Target Language",
                    trailingText = userProfile.targetLanguage,
                    onClick = {}
                )
                SidebarItem(
                    icon = Icons.Filled.Language,
                    title = "Native Language",
                    trailingText = userProfile.nativeLanguage,
                    onClick = {}
                )"""
content = content.replace(native_sidebar, target_sidebar)
with open(path, "w") as f:
    f.write(content)

# Settings
path2 = "app/src/main/java/com/example/ui/screens/SettingsScreen.kt"
with open(path2, "r") as f:
    content2 = f.read()

native_settings = """                SettingsItem(
                    icon = Icons.Filled.Language,
                    title = "Native Language",
                    subtitle = userProfile.nativeLanguage,
                    onClick = {}
                )"""
target_settings = """                SettingsItem(
                    icon = Icons.Filled.Language,
                    title = "Target Language",
                    subtitle = userProfile.targetLanguage,
                    onClick = {}
                )
                SettingsItem(
                    icon = Icons.Filled.Language,
                    title = "Native Language",
                    subtitle = userProfile.nativeLanguage,
                    onClick = {}
                )"""
content2 = content2.replace(native_settings, target_settings)
with open(path2, "w") as f:
    f.write(content2)

