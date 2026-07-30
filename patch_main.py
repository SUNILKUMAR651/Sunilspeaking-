import re

path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

old_nav = """            composable("weak_words") {
                WeakWordsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }"""

new_nav = """            composable("weak_words") {
                WeakWordsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("pronunciation_history") {
                PronunciationHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }"""

content = content.replace(old_nav, new_nav)

with open(path, "w") as f:
    f.write(content)
