import re

path = "app/src/main/java/com/example/ui/screens/SettingsScreen.kt"
with open(path, "r") as f:
    content = f.read()

old_theme = """                val currentTheme = userProfile.themePreference"""

new_theme = """                SettingsToggleItem(
                    icon = Icons.Filled.NotificationsActive,
                    iconBgColor = Color(0xFFFF5252),
                    title = "Daily Reminders",
                    subtitle = "Get notified to complete your lesson",
                    checked = userProfile.notificationsEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.updateProfile(userProfile.copy(notificationsEnabled = enabled))
                        if (enabled) {
                            com.example.utils.NotificationHelper.scheduleDailyReminder(context, 18, 0)
                        } else {
                            com.example.utils.NotificationHelper.cancelReminder(context)
                        }
                    }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                
                val currentTheme = userProfile.themePreference"""

content = content.replace(old_theme, new_theme)

with open(path, "w") as f:
    f.write(content)
