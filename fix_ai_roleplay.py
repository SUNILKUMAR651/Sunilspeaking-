path = "app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt"
with open(path, 'r') as f:
    content = f.read()

content = content.replace("fun ActiveRoleplayScreen(scenario: RoleplayScenario, viewModel: LexiViewModel, onBack: () -> Unit) {", "fun ActiveRoleplayScreen(scenario: RoleplayScenario, viewModel: LexiViewModel, onBack: () -> Unit) {\n    val userProfile by viewModel.userProfile.collectAsState()")

with open(path, 'w') as f:
    f.write(content)
