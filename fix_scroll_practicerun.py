path = "app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt"
with open(path, 'r') as f:
    content = f.read()

import re

# Add imports for verticalScroll
if "import androidx.compose.foundation.rememberScrollState" not in content:
    content = content.replace("import androidx.compose.foundation.clickable", "import androidx.compose.foundation.clickable\nimport androidx.compose.foundation.rememberScrollState\nimport androidx.compose.foundation.verticalScroll")

# Add verticalScroll to the Column in PracticeRunContent
content = content.replace("Column(modifier = Modifier.padding(24.dp)) {", "Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {")

with open(path, 'w') as f:
    f.write(content)
