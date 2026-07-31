import re
path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

bad_block = """    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            com.example.utils.NotificationHelper.scheduleDailyReminder(context, 18, 0) // 6 PM
        }
    }
    
    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                com.example.utils.NotificationHelper.scheduleDailyReminder(context, 18, 0)
            }
        } else {
            com.example.utils.NotificationHelper.scheduleDailyReminder(context, 18, 0)
        }
    }"""

good_block = """    val userProfile by viewModel.userProfile.collectAsState()
    
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && userProfile.notificationsEnabled) {
            com.example.utils.NotificationHelper.scheduleDailyReminder(context, userProfile.reminderHour, userProfile.reminderMinute)
        }
    }
    
    LaunchedEffect(userProfile.notificationsEnabled, userProfile.reminderHour, userProfile.reminderMinute) {
        if (!userProfile.notificationsEnabled) {
            com.example.utils.NotificationHelper.cancelReminder(context)
            return@LaunchedEffect
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                com.example.utils.NotificationHelper.scheduleDailyReminder(context, userProfile.reminderHour, userProfile.reminderMinute)
            }
        } else {
            com.example.utils.NotificationHelper.scheduleDailyReminder(context, userProfile.reminderHour, userProfile.reminderMinute)
        }
    }"""

content = content.replace(bad_block, good_block)

with open(path, "w") as f:
    f.write(content)
