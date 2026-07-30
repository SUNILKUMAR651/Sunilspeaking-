import re

path = "app/src/main/java/com/example/ui/screens/OnboardingScreen.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("var step by remember { mutableIntStateOf(1) }", "var step by remember { mutableIntStateOf(0) }")

content = content.replace("var selectedLanguage by remember { mutableStateOf(userProfile.nativeLanguage) }", "var selectedLanguage by remember { mutableStateOf(userProfile.nativeLanguage) }\n    var selectedTarget by remember { mutableStateOf(userProfile.targetLanguage) }")

level_texts = """        "Beginner (A1)" to "Just starting to learn English",
        "Elementary (A2)" to "Can understand basic phrases",
        "Intermediate (B1)" to "Can hold a basic conversation",
        "Upper Int. (B2)" to "Can speak fluently on most topics",
        "Advanced (C1)" to "Can express complex ideas easily",
        "Master (C2)" to "Native-like proficiency" """

new_level_texts = """        "Beginner (A1)" to "Just starting to learn",
        "Elementary (A2)" to "Can understand basic phrases",
        "Intermediate (B1)" to "Can hold a basic conversation",
        "Upper Int. (B2)" to "Can speak fluently on most topics",
        "Advanced (C1)" to "Can express complex ideas easily",
        "Master (C2)" to "Native-like proficiency" """
content = content.replace(level_texts, new_level_texts)


progress_bar = """        LinearProgressIndicator(
            progress = { step / 2f },"""
new_progress_bar = """        LinearProgressIndicator(
            progress = { (step + 1) / 3f },"""
content = content.replace(progress_bar, new_progress_bar)

step_logic = """        if (step == 1) {
            Text(
                text = "What is your native language?","""
new_step_logic = """        if (step == 0) {
            Text(
                text = "What language do you want to learn?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Choose your target language.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                languages.forEach { lang ->
                    val isSelected = selectedTarget == lang
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { selectedTarget = lang },
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isSelected) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)),
                        color = if (isSelected) Color(0xFFDDF4FF) else Color.White
                    ) {
                        Text(
                            text = lang,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF1CB0F6) else Color(0xFF4B4B4B),
                            modifier = Modifier.padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else if (step == 1) {
            Text(
                text = "What is your native language?","""
content = content.replace(step_logic, new_step_logic)


level_question = """        } else {
            Text(
                text = "What is your English level?","""
new_level_question = """        } else {
            Text(
                text = "What is your $selectedTarget level?", """
content = content.replace(level_question, new_level_question)


button_logic = """        Button(
            onClick = {
                if (step == 1) {
                    step = 2
                } else {
                    viewModel.updateProfile(userProfile.copy(
                        nativeLanguage = selectedLanguage,
                        level = selectedLevel,
                        isOnboardingCompleted = true
                    ))
                    onFinish()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (step == 1) Color(0xFF1CB0F6) else Color(0xFF58CC02))
        ) {
            Text(if (step == 1) "CONTINUE" else "START LEARNING", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }"""
new_button_logic = """        Button(
            onClick = {
                if (step < 2) {
                    step++
                } else {
                    viewModel.updateProfile(userProfile.copy(
                        targetLanguage = selectedTarget,
                        nativeLanguage = selectedLanguage,
                        level = selectedLevel,
                        isOnboardingCompleted = true
                    ))
                    onFinish()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (step < 2) Color(0xFF1CB0F6) else Color(0xFF58CC02))
        ) {
            Text(if (step < 2) "CONTINUE" else "START LEARNING", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }"""
content = content.replace(button_logic, new_button_logic)


with open(path, "w") as f:
    f.write(content)
