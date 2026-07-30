import re

path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

old_theme = """        setContent {
            LexiCoreTheme {
                val viewModel: LexiViewModel = viewModel()
                val authStatus by viewModel.authStatus.collectAsState()"""

new_theme = """        setContent {
            val viewModel: LexiViewModel = viewModel()
            val userProfile by viewModel.userProfile.collectAsState()
            val darkTheme = when (userProfile.themePreference) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            LexiCoreTheme(darkTheme = darkTheme) {
                val authStatus by viewModel.authStatus.collectAsState()"""

content = content.replace(old_theme, new_theme)

with open(path, "w") as f:
    f.write(content)
