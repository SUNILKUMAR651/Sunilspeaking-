path = "app/src/main/java/com/example/ui/screens/LeaderboardScreen.kt"
with open(path, 'r') as f: content = f.read()

content = content.replace("userProfile.", "profile.")
content = content.replace("val profile by viewModel.profile.collectAsState()", "")

# Wait, `val userProfile by viewModel.userProfile.collectAsState()` was injected.
# We should replace it with ""
content = content.replace("val userProfile by viewModel.userProfile.collectAsState()\n", "")

with open(path, 'w') as f: f.write(content)
