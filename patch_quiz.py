import re
path = "app/src/main/java/com/example/ui/screens/VocabularyQuizScreen.kt"
with open(path, "r") as f:
    content = f.read()

import_block = """import java.util.Locale"""
new_import = """import java.util.Locale
import com.airbnb.lottie.compose.*
import com.example.R"""
content = content.replace(import_block, new_import)

state_block = """    var timeLeft by remember { mutableIntStateOf(15) }
    
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }"""
new_state = """    var timeLeft by remember { mutableIntStateOf(15) }
    
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    
    var showSuccessAnimation by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.heart))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = showSuccessAnimation,
        restartOnPlay = false
    )
    
    LaunchedEffect(progress) {
        if (progress == 1f) {
            showSuccessAnimation = false
        }
    }"""
content = content.replace(state_block, new_state)

action_block = """                            if (selectedAnswer == question.correctAnswer) {
                                score++
                                toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
                            } else {"""
new_action = """                            if (selectedAnswer == question.correctAnswer) {
                                score++
                                toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
                                showSuccessAnimation = true
                            } else {"""
content = content.replace(action_block, new_action)

ui_block = """    Scaffold(
        topBar = {"""
new_ui = """    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        topBar = {"""
content = content.replace(ui_block, new_ui)

ui_end_block = """            }
        }
    }
}

data class QuizQuestion("""
new_ui_end = """            }
        }
    }
    
    if (showSuccessAnimation) {
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(250.dp)
            )
        }
    }
    }
}

data class QuizQuestion("""
content = content.replace(ui_end_block, new_ui_end)

with open(path, "w") as f:
    f.write(content)
