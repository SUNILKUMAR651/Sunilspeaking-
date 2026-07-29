import re

with open('app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt', 'r') as f:
    content = f.read()

sealed_class_regex = r'sealed class LessonExercise \{.*?\n\}'
old_sealed_class_match = re.search(sealed_class_regex, content, re.DOTALL)
if old_sealed_class_match:
    old_sealed_class = old_sealed_class_match.group(0)
    new_sealed_class = '''sealed class LessonExercise {
    data class NewWord(val word: String, val sentence: String) : LessonExercise()
    data class Listening(val audioText: String, val options: List<String>, val correctOption: String) : LessonExercise()
    data class SpeakingSentence(val sentence: String) : LessonExercise()
    data class Translation(val prompt: String, val options: List<String>, val correctOption: String) : LessonExercise()
    data class ArrangeWords(val prompt: String, val correctSentence: String, val shuffledWords: List<String>) : LessonExercise()
    data class FillInTheBlanks(val prompt: String, val sentenceParts: List<String>, val correctWord: String, val options: List<String>) : LessonExercise()
    data class MatchPairs(val prompt: String, val pairs: Map<String, String>) : LessonExercise()
    data class PhraseNarration(val title: String, val phrase: String, val explanation: String) : LessonExercise()
}'''
    content = content.replace(old_sealed_class, new_sealed_class)

exercises_regex = r'val exercises = remember \{.*?\n        generated\n    \}'
old_exercises_match = re.search(exercises_regex, content, re.DOTALL)
if old_exercises_match:
    old_exercises = old_exercises_match.group(0)
    new_exercises = '''val exercises = remember {
        val difficulty = (lessonId / 50) + 1
        val isProLevel = lessonId > 10
        val generated = mutableListOf<LessonExercise>()
        
        // Dynamic content based on level
        val words = if (difficulty < 3) listOf("Hello", "Name", "Friend", "Good") else listOf("Challenge", "Opportunity", "Perseverance", "Achievement")
        val advancedWords = listOf("Incomprehensible", "Magnificent", "Exquisite", "Flawless")
        
        // Increase number of exercises based on difficulty (10 to 20)
        val exerciseCount = 10 + (difficulty * 2).coerceAtMost(10)
        
        for (i in 0 until exerciseCount) {
            val typeRand = (0..100).random()
            
            // Gradually introduce harder exercises based on difficulty
            if (i == 0) {
                generated.add(LessonExercise.PhraseNarration(
                    title = "Grammar & Tense Focus",
                    phrase = if (difficulty > 3) "Past Perfect Continuous" else "Present Simple",
                    explanation = if (difficulty > 3) "Used to describe an action that started in the past and continued up until another time in the past." else "Used for facts, habits, and general truths."
                ))
            } else if (typeRand < 15) {
                val word = if (difficulty > 5) advancedWords.random() else words.random()
                generated.add(LessonExercise.NewWord(word, "Let's learn: $word"))
            } else if (typeRand < 30) {
                val pairs = if (difficulty > 4) mapOf("Big" to "Huge", "Fast" to "Quick", "Smart" to "Clever", "Hard" to "Difficult") else mapOf("Dog" to "Perro", "Cat" to "Gato", "Sun" to "Sol", "Moon" to "Luna")
                generated.add(LessonExercise.MatchPairs("Match the correct pairs", pairs))
            } else if (typeRand < 45) {
                val sentence = if (difficulty > 4) "The meticulous attention to detail is truly impressive." else "My name is Anna and I am happy."
                generated.add(LessonExercise.ArrangeWords("Arrange to form the correct sentence", sentence.lowercase().replace(".", ""), sentence.split(" ").shuffled()))
            } else if (typeRand < 60) {
                generated.add(LessonExercise.FillInTheBlanks("Complete the tense", listOf("I ", " to the store yesterday."), "went", listOf("go", "went", "going", "gone")))
            } else if (typeRand < 75) {
                val word = if (difficulty > 5) advancedWords.random() else words.random()
                generated.add(LessonExercise.Translation("Translate '$word'", listOf(word, "Random", "Other", "Wrong").shuffled(), word))
            } else if (typeRand < 90) {
                val word = if (difficulty > 5) advancedWords.random() else words.random()
                generated.add(LessonExercise.Listening("Listen carefully", listOf(word, "Something", "Nothing", "Everything").shuffled(), word))
            } else {
                val sentence = if (difficulty > 4) "It is imperative that we proceed with caution." else "I am learning a new language."
                generated.add(LessonExercise.SpeakingSentence(sentence))
            }
        }

        generated
    }'''
    content = content.replace(old_exercises, new_exercises)

when_regex = r'when \(exercise\) \{.*?\n            \}'
old_when_match = re.search(when_regex, content, re.DOTALL)
if old_when_match:
    old_when = old_when_match.group(0)
    new_when = '''when (exercise) {
                is LessonExercise.NewWord -> {
                    NewWordView(exercise) { currentIndex++ }
                }
                is LessonExercise.Listening -> {
                    ListeningView(exercise) { currentIndex++ }
                }
                is LessonExercise.SpeakingSentence -> {
                    SpeakingSentenceView(exercise) { currentIndex++ }
                }
                is LessonExercise.Translation -> {
                    TranslationView(exercise) { currentIndex++ }
                }
                is LessonExercise.ArrangeWords -> {
                    ArrangeWordsView(exercise) { currentIndex++ }
                }
                is LessonExercise.FillInTheBlanks -> {
                    FillInTheBlanksView(exercise) { currentIndex++ }
                }
                is LessonExercise.MatchPairs -> {
                    MatchPairsView(exercise) { currentIndex++ }
                }
                is LessonExercise.PhraseNarration -> {
                    PhraseNarrationView(exercise) { currentIndex++ }
                }
            }'''
    content = content.replace(old_when, new_when)

with open('app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt', 'w') as f:
    f.write(content)
