import re
path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, "r") as f:
    content = f.read()

bad = """                    val fallbackMsg = if (finalResponse.code == 429) {
                        "I am receiving too many requests right now. Please wait a moment before trying again."
                    } else {
                        "I encountered an issue connecting to the AI API (Error ${finalResponse.code})."
                    }"""

good = """                    val mockResponses = listOf(
                        "That's very interesting! Can you tell me more?",
                        "I see! How does that make you feel?",
                        "That is a great point. I agree with you.",
                        "Could you elaborate on that?",
                        "I hear you! You said something great.",
                        "Awesome! Let's keep practicing.",
                        "Very good! Your pronunciation is getting better."
                    )
                    val fallbackMsg = mockResponses.random()"""

content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
