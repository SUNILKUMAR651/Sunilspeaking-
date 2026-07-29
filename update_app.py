import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Make sure SidebarMenu is imported
if 'import com.example.ui.screens.SidebarMenu' not in content:
    content = content.replace('import com.example.ui.screens.SmartReviewScreen', 'import com.example.ui.screens.SmartReviewScreen\nimport com.example.ui.screens.SidebarMenu\nimport kotlinx.coroutines.launch')

# Rewrite LexiApp signature and wrapping
old_lexiapp_start = """fun LexiApp(viewModel: LexiViewModel) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("home") }

    Scaffold("""

new_lexiapp_start = """fun LexiApp(viewModel: LexiViewModel) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("home") }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarMenu(
                viewModel = viewModel,
                onNavigate = { route ->
                    currentRoute = route
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onCloseDrawer = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold("""

content = content.replace(old_lexiapp_start, new_lexiapp_start)

# Now find the end of LexiApp to add the closing bracket for ModalNavigationDrawer
# Since LexiApp ends with:
#             }
#         }
#     }
# }
# We replace the last "    }\n}" with "        }\n    }\n}"

if new_lexiapp_start in content:
    content = content.rsplit('    }\n}', 1)
    content = '    }\n    }\n}'.join(content)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

