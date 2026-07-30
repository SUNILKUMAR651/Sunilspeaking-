import re

path = "app/src/main/java/com/example/viewmodel/LexiViewModel.kt"
with open(path, "r") as f:
    content = f.read()

call_stmt = """            val result = repository.generateExampleSentence(word, _userInterest.value)"""
new_call_stmt = """            val result = repository.generateExampleSentence(word, _userInterest.value, _userProfile.value.targetLanguage)"""
content = content.replace(call_stmt, new_call_stmt)

with open(path, "w") as f:
    f.write(content)
