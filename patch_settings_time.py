import re
path = "app/src/main/java/com/example/ui/screens/SettingsScreen.kt"
with open(path, "r") as f:
    content = f.read()

bad_block = """                SettingsToggleItem(
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
                )"""

good_block = """                SettingsToggleItem(
                    icon = Icons.Filled.NotificationsActive,
                    iconBgColor = Color(0xFFFF5252),
                    title = "Daily Reminders",
                    subtitle = "Get notified to complete your lesson",
                    checked = userProfile.notificationsEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.updateProfile(userProfile.copy(notificationsEnabled = enabled))
                        if (enabled) {
                            com.example.utils.NotificationHelper.scheduleDailyReminder(context, userProfile.reminderHour, userProfile.reminderMinute)
                        } else {
                            com.example.utils.NotificationHelper.cancelReminder(context)
                        }
                    }
                )
                
                if (userProfile.notificationsEnabled) {
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    
                    val timeString = String.format("%02d:%02d", userProfile.reminderHour, userProfile.reminderMinute)
                    SettingsItem(
                        icon = Icons.Filled.AccessTime,
                        iconBgColor = Color(0xFF4CAF50),
                        title = "Reminder Time",
                        subtitle = "Select when to be notified",
                        trailingText = timeString,
                        onClick = { 
                            android.app.TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    viewModel.updateProfile(userProfile.copy(reminderHour = hour, reminderMinute = minute))
                                    com.example.utils.NotificationHelper.scheduleDailyReminder(context, hour, minute)
                                },
                                userProfile.reminderHour,
                                userProfile.reminderMinute,
                                true
                            ).show()
                        }
                    )
                }"""

content = content.replace(bad_block, good_block)

with open(path, "w") as f:
    f.write(content)
