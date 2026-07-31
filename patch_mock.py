import re
path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, "r") as f:
    content = f.read()

bad = """                val mockResponseJson = \"\"\"
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "This is an automatic mock response because no API key was provided. To get real AI responses, please configure your Gemini API Key in the settings or Secrets panel."
                          }
                        ]
                      }
                    }
                  ]
                }
                \"\"\".trimIndent()"""

good = """                val mockResponses = listOf(
                    "That's very interesting! Can you tell me more?",
                    "I see! How does that make you feel?",
                    "That is a great point. I agree with you.",
                    "Could you elaborate on that?",
                    "I'm currently in offline mock mode, but I hear you clearly! You said something great.",
                    "Awesome! Let's keep practicing.",
                    "Very good! Your pronunciation is getting better."
                )
                val randomResponse = mockResponses.random()
                val mockResponseJson = \"\"\"
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "${randomResponse}"
                          }
                        ]
                      }
                    }
                  ]
                }
                \"\"\".trimIndent()"""

content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
