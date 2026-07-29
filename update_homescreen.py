import re

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'r') as f:
    content = f.read()

# Add onOpenDrawer to HomeScreen signature
content = content.replace(
'''fun HomeScreen(
    viewModel: LexiViewModel,
    onNavigate: (String) -> Unit
) {''',
'''fun HomeScreen(
    viewModel: LexiViewModel,
    onNavigate: (String) -> Unit,
    onOpenDrawer: () -> Unit = {}
) {''')

# Now add IconButton for the menu
top_bar_search = '''                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface('''
top_bar_replace = '''                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onOpenDrawer) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface('''
content = content.replace(top_bar_search, top_bar_replace)

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'w') as f:
    f.write(content)

