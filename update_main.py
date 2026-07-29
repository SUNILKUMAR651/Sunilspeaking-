with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace(
'''            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { route ->''',
'''            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onNavigate = { route ->''')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
