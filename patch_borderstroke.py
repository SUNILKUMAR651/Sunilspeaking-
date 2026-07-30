import re
path = "app/src/main/java/com/example/ui/components/GameComponents.kt"
with open(path, "r") as f:
    content = f.read()

import_str = "import androidx.compose.foundation.border"
new_import_str = """import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke"""
content = content.replace(import_str, new_import_str)

with open(path, "w") as f:
    f.write(content)
