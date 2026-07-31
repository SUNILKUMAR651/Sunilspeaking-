import re
path = "app/src/main/java/com/example/ui/screens/GrammarChallengeScreen.kt"
with open(path, "r") as f:
    content = f.read()

import_block = """import androidx.compose.foundation.layout.*"""
new_import = """import androidx.compose.foundation.layout.*
import android.media.AudioManager
import android.media.ToneGenerator
import com.airbnb.lottie.compose.*
import com.example.R"""
content = content.replace(import_block, new_import)

state_block = """    var score by remember { mutableStateOf(0) }"""
new_state = """    var score by remember { mutableStateOf(0) }
    
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    DisposableEffect(Unit) {
        onDispose { toneGen.release() }
    }
    
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

ui_block = """    Scaffold("""
new_ui = """    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold("""
content = content.replace(ui_block, new_ui)

action_block = """                            if (selectedAnswer == question.correctAnswer) {
                                score++
                            }
                            showResult = true"""
new_action = """                            if (selectedAnswer == question.correctAnswer) {
                                score++
                                toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
                                showSuccessAnimation = true
                            } else {
                                toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 300)
                            }
                            showResult = true"""
content = content.replace(action_block, new_action)

ui_end_block = """            }
        }
    }
}

data class GrammarQuestion("""
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

data class GrammarQuestion("""
content = content.replace(ui_end_block, new_ui_end)

with open(path, "w") as f:
    f.write(content)
