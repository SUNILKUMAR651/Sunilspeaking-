import re
path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, "r") as f:
    content = f.read()

import_block = """import com.example.viewmodel.LexiViewModel"""
new_import = """import com.example.viewmodel.LexiViewModel
import com.airbnb.lottie.compose.*
import com.example.R
import kotlinx.coroutines.delay"""
content = content.replace(import_block, new_import)

state_block = """    val userProfile by viewModel.userProfile.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()"""
new_state = """    val userProfile by viewModel.userProfile.collectAsState()
    val showGoalAnimation by viewModel.showGoalAnimation.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.heart))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = showGoalAnimation,
        restartOnPlay = false
    )
    
    LaunchedEffect(progress) {
        if (progress == 1f && showGoalAnimation) {
            viewModel.onGoalAnimationShown()
        }
    }"""
content = content.replace(state_block, new_state)

ui_block = """            modifier = Modifier.fillMaxSize()
        ) { padding ->"""
new_ui = """            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {"""
content = content.replace(ui_block, new_ui)

ui_end_block = """            }
        }
    }
}

@Composable"""
new_ui_end = """            }
            
            if (showGoalAnimation) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.6f)).clickable { viewModel.onGoalAnimationShown() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(300.dp)
                        )
                        Text("Daily Goal Achieved! +XP", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            }
        }
    }
}

@Composable"""
content = content.replace(ui_end_block, new_ui_end)

with open(path, "w") as f:
    f.write(content)
