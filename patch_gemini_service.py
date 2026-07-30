import re
path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, "r") as f:
    content = f.read()

content_str = """@Serializable
data class Content(
    val parts: List<Part>
)"""

new_content_str = """@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)"""

content = content.replace(content_str, new_content_str)

with open(path, "w") as f:
    f.write(content)
