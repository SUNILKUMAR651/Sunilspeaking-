path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, 'r') as f:
    content = f.read()

import re

old_routes = """                        composable("pro_practice") {
                ProPracticeScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToRun = { scenarioId ->
                        navController.navigate("pro_practice_run/$scenarioId")
                    }
                )
            }
            composable("pro_practice_run/{scenarioId}") { backStackEntry ->
                val scenarioId = backStackEntry.arguments?.getString("scenarioId")?.toIntOrNull() ?: 0
                PracticeRunScreen(
                    lessonId = scenarioId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }"""

new_routes = """                        composable("pro_practice") {
                com.example.ui.screens.SpeakingPracticeDashboard(
                    onBack = { navController.popBackStack() },
                    onNavigateToCategory = { categoryId ->
                        navController.navigate("pro_practice_category/$categoryId")
                    }
                )
            }
            composable("pro_practice_category/{categoryId}") { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "job_interview"
                com.example.ui.screens.SpeakingTopicDetailScreen(
                    categoryId = categoryId,
                    onBack = { navController.popBackStack() },
                    onNavigateToTask = { taskId ->
                        navController.navigate("pro_practice_run/$taskId")
                    }
                )
            }
            composable("pro_practice_run/{taskId}") { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull() ?: 1
                com.example.ui.screens.SpeakingTaskRunScreen(
                    taskId = taskId,
                    onBack = { navController.popBackStack() }
                )
            }"""

content = content.replace(old_routes, new_routes)

with open(path, 'w') as f:
    f.write(content)
