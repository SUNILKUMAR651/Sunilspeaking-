import re

path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

old_auth_check = """                if (authStatus is com.example.viewmodel.AuthStatus.Authenticated) {
                    LexiApp(viewModel = viewModel)
                } else {
                    AuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = { }
                    )
                }"""

new_auth_check = """                if (authStatus is com.example.viewmodel.AuthStatus.Authenticated) {
                    val userProfile by viewModel.userProfile.collectAsState()
                    if (userProfile.isOnboardingCompleted) {
                        LexiApp(viewModel = viewModel)
                    } else {
                        OnboardingScreen(
                            viewModel = viewModel,
                            onFinish = { }
                        )
                    }
                } else {
                    AuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = { }
                    )
                }"""

content = content.replace(old_auth_check, new_auth_check)

with open(path, "w") as f:
    f.write(content)
