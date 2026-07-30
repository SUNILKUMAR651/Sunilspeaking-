import re

path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

old_lexiapp = """@Composable
fun LexiApp(viewModel: LexiViewModel) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("home") }"""

new_lexiapp = """@Composable
fun LexiApp(viewModel: LexiViewModel) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("home") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
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

content = content.replace(old_lexiapp, new_lexiapp)

with open(path, "w") as f:
    f.write(content)
