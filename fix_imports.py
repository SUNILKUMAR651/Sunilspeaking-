path = "app/src/main/java/com/example/ui/screens/SpeakingTaskRunScreen.kt"
with open(path, 'r') as f:
    content = f.read()

content = content.replace("import androidx.compose.foundation.rememberScrollState\nimport androidx.compose.foundation.verticalScroll\npackage com.example.ui.screens", "package com.example.ui.screens\nimport androidx.compose.foundation.rememberScrollState\nimport androidx.compose.foundation.verticalScroll\n")

with open(path, 'w') as f:
    f.write(content)
