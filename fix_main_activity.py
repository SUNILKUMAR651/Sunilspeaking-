with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

import_str = "import com.example.ui.screens.*\n"
if "import com.example.ui.screens.ProPracticeScreen" not in content:
    content = content.replace("import com.example.ui.screens.*", import_str + "import com.example.ui.screens.ProPracticeScreen\nimport com.example.ui.screens.PracticeRunScreen\n")

route_code = """
            composable("certifications") {
                CertificationsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("pro_practice") {
                ProPracticeScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToRun = { id ->
                        navController.navigate("practice_run/$id")
                    }
                )
            }
            
            composable("practice_run/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
                PracticeRunScreen(
                    lessonId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
"""

if "composable(\"pro_practice\")" not in content:
    content = content.replace("""composable("certifications") {
                CertificationsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }""", route_code)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
