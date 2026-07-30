import re
path = "app/src/main/java/com/example/ui/screens/GameScreens.kt"
with open(path, "r") as f:
    content = f.read()

import_str = "import com.example.viewmodel.LexiViewModel"
new_import_str = """import com.example.viewmodel.LexiViewModel
import com.example.ui.components.CrosswordConnectGameLayout
import com.example.ui.components.WordWheelGameLayout"""
content = content.replace(import_str, new_import_str)

crossword_str = """@Composable
fun CrosswordConnectScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    SimpleGameScreenPlaceholder("Crossword Connect", onBack)
}"""

new_crossword_str = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordConnectScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    var isComplete by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crossword Connect", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isComplete) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Level Complete!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Finish")
                    }
                }
            } else {
                CrosswordConnectGameLayout(
                    letterBank = listOf("A", "E", "S", "R", "C", "H"),
                    validWords = listOf("SEARCH", "CHASE", "RACE", "CARE", "EACH"),
                    onAllWordsFound = {
                        isComplete = true
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordWheelScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    var isComplete by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Wheel", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isComplete) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Word Found!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Finish")
                    }
                }
            } else {
                WordWheelGameLayout(
                    sentenceWithBlank = "The company will [BLANK] all new employees tomorrow.",
                    targetCorrectWord = "WELCOME",
                    rolls = listOf(
                        listOf("W", "M", "V", "N"),
                        listOf("E", "A", "O", "I"),
                        listOf("L", "I", "T", "D"),
                        listOf("C", "K", "S", "G"),
                        listOf("O", "U", "E", "A"),
                        listOf("M", "N", "W", "B"),
                        listOf("E", "I", "A", "Y")
                    ),
                    onCorrectSubmit = {
                        isComplete = true
                    }
                )
            }
        }
    }
}"""
content = content.replace(crossword_str, new_crossword_str)

with open(path, "w") as f:
    f.write(content)
