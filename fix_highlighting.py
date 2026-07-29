path = "app/src/main/java/com/example/ui/screens/SpeakingTaskRunScreen.kt"
with open(path, 'r') as f:
    content = f.read()

import re

old_annotated = """            val annotatedString = buildAnnotatedString {
                val words = targetText.split(" ")
                words.forEach { word ->
                    val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                    if (recognizedText.isNotEmpty() && recognizedWords.contains(cleanWord)) {
                        withStyle(style = SpanStyle(color = Color(0xFF00C48C))) { append("$word ") }
                    } else if (recognizedText.isNotEmpty()) {
                        withStyle(style = SpanStyle(color = Color.Black)) { append("$word ") }
                    } else {
                        withStyle(style = SpanStyle(color = Color(0xFF1E293B))) { append("$word ") }
                    }
                }
            }"""

new_annotated = """            val annotatedString = buildAnnotatedString {
                val words = targetText.split(" ")
                var currentMatchIndex = 0
                
                words.forEachIndexed { index, word ->
                    val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                    
                    if (recognizedText.isEmpty()) {
                        withStyle(style = SpanStyle(color = Color(0xFF1E293B))) { append("$word ") }
                    } else {
                        // Check if this word was spoken
                        // We will just do a simple check: if the word exists in the spoken text, mark green.
                        // If we have spoken N words, and this is the Nth word and it doesn't match, mark red.
                        
                        val isMatched = recognizedWords.contains(cleanWord)
                        
                        if (isMatched) {
                            withStyle(style = SpanStyle(color = Color(0xFF00C48C), fontWeight = FontWeight.Bold)) { append("$word ") }
                        } else {
                            // If it's not matched, but it's within the count of words spoken so far, mark it red as a mistake
                            // We use a heuristic: if recognizedWords has X words, we assume they tried to read up to word X
                            if (index < recognizedWords.size) {
                                withStyle(style = SpanStyle(color = Color(0xFFFF3333), textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) { append("$word ") }
                            } else {
                                withStyle(style = SpanStyle(color = Color(0xFF1E293B))) { append("$word ") }
                            }
                        }
                    }
                }
            }"""

content = content.replace(old_annotated, new_annotated)

with open(path, 'w') as f:
    f.write(content)
