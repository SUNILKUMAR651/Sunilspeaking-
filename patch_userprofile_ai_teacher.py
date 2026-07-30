import re

path = "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt"
with open(path, "r") as f:
    content = f.read()

insert_point = """fun AITeacherScreen(viewModel: LexiViewModel, onNavigateToCall: () -> Unit) {
    val context = LocalContext.current"""
new_insert = """fun AITeacherScreen(viewModel: LexiViewModel, onNavigateToCall: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current"""
content = content.replace(insert_point, new_insert)

with open(path, "w") as f:
    f.write(content)
