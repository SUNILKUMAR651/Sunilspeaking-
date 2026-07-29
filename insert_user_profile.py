import re
import os

def insert_profile(filepath, screen_name):
    with open(filepath, 'r') as f:
        content = f.read()
    
    if "val userProfile by viewModel.userProfile.collectAsState()" not in content:
        target = f"fun {screen_name}("
        idx = content.find(target)
        if idx != -1:
            # find first '{' after the signature
            brace_idx = content.find('{', idx)
            if brace_idx != -1:
                content = content[:brace_idx+1] + "\n    val userProfile by viewModel.userProfile.collectAsState()" + content[brace_idx+1:]
                with open(filepath, 'w') as f:
                    f.write(content)
                print(f"Fixed {screen_name}")

insert_profile("app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt", "AIRoleplayScreen")
insert_profile("app/src/main/java/com/example/ui/screens/VocabularyQuizScreen.kt", "VocabularyQuizScreen")
insert_profile("app/src/main/java/com/example/ui/screens/AICallScreen.kt", "AICallScreen")
insert_profile("app/src/main/java/com/example/ui/screens/VocabularyArrangementScreen.kt", "VocabularyArrangementScreen")
insert_profile("app/src/main/java/com/example/ui/speaking/SpeakingEvaluationScreen.kt", "SpeakingEvaluationScreen")
