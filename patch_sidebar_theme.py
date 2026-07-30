import re

path = "app/src/main/java/com/example/ui/screens/SidebarMenu.kt"
with open(path, "r") as f:
    content = f.read()

old_female_voice = """                SidebarToggleItem(
                    icon = Icons.Filled.RecordVoiceOver,
                    iconBgColor = Color(0xFFCE82FF),
                    title = "AI Voice (Female)",
                    subtitle = "Use female voice for lessons",
                    checked = userProfile.useFemaleVoice,
                    onCheckedChange = { isFemale ->
                        viewModel.updateProfile(userProfile.copy(useFemaleVoice = isFemale))
                    }
                )
            }"""

new_female_voice = """                SidebarToggleItem(
                    icon = Icons.Filled.RecordVoiceOver,
                    iconBgColor = Color(0xFFCE82FF),
                    title = "AI Voice (Female)",
                    subtitle = "Use female voice for lessons",
                    checked = userProfile.useFemaleVoice,
                    onCheckedChange = { isFemale ->
                        viewModel.updateProfile(userProfile.copy(useFemaleVoice = isFemale))
                    }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                
                val currentTheme = userProfile.themePreference
                val nextTheme = when(currentTheme) {
                    "light" -> "dark"
                    "dark" -> "system"
                    else -> "light"
                }
                val themeLabel = when(currentTheme) {
                    "light" -> "Light Mode"
                    "dark" -> "Dark Mode"
                    else -> "System Theme"
                }
                
                SidebarItem(
                    icon = Icons.Filled.DarkMode,
                    iconBgColor = Color(0xFF6B4EE6),
                    title = "App Theme",
                    subtitle = "Current: $themeLabel",
                    onClick = { 
                        viewModel.updateProfile(userProfile.copy(themePreference = nextTheme))
                    }
                )
            }"""

content = content.replace(old_female_voice, new_female_voice)

with open(path, "w") as f:
    f.write(content)
