import re

path = "app/src/main/java/com/example/data/repository/LexiRepository.kt"
with open(path, "r") as f:
    content = f.read()

func_def = """    suspend fun generateExampleSentence(word: String, interest: String): Result<String> = withContext(Dispatchers.IO) {"""
new_func_def = """    suspend fun generateExampleSentence(word: String, interest: String, targetLanguage: String = "English"): Result<String> = withContext(Dispatchers.IO) {"""
content = content.replace(func_def, new_func_def)

sys_prompt = """            systemInstruction = Content(parts = listOf(Part(text = "You are LexiMaster, an expert English language coach.")))"""
new_sys_prompt = """            systemInstruction = Content(parts = listOf(Part(text = "You are LexiMaster, an expert $targetLanguage language coach.")))"""
content = content.replace(sys_prompt, new_sys_prompt)

with open(path, "w") as f:
    f.write(content)
