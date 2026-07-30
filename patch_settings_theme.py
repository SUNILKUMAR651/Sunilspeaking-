import re

path = "app/src/main/java/com/example/ui/screens/SettingsScreen.kt"
with open(path, "r") as f:
    content = f.read()

old_learning_goal = """                SettingsItem(
                    icon = Icons.Filled.Description,
                    iconBgColor = Color(0xFFFF89B3),
                    title = "Learning Goal",
                    subtitle = "Tap to choose your learning objective",
                    trailingText = "Pass Exams",
                    onClick = { }
                )
            }"""

new_learning_goal = """                SettingsItem(
                    icon = Icons.Filled.Description,
                    iconBgColor = Color(0xFFFF89B3),
                    title = "Learning Goal",
                    subtitle = "Tap to choose your learning objective",
                    trailingText = "Pass Exams",
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                
                val currentTheme = userProfile.themePreference
                val nextTheme = when(currentTheme) {
                    "light" -> "dark"
                    "dark" -> "system"
                    else -> "light"
                }
                val themeLabel = when(currentTheme) {
                    "light" -> "Light"
                    "dark" -> "Dark"
                    else -> "System"
                }
                
                SettingsItem(
                    icon = Icons.Filled.DarkMode,
                    iconBgColor = Color(0xFF6B4EE6),
                    title = "App Theme",
                    subtitle = "Tap to change theme",
                    trailingText = themeLabel,
                    onClick = { 
                        viewModel.updateProfile(userProfile.copy(themePreference = nextTheme))
                    }
                )
            }"""

content = content.replace(old_learning_goal, new_learning_goal)

with open(path, "w") as f:
    f.write(content)
