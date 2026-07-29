letters = "FGHIJKLMNOPQRSTUVWXYZ"
words = []

for l in letters:
    words.append(f"""
        WordObject(
            word = "{l}Sample",
            phonetic = "/{l.lower()}æmpəl/",
            partOfSpeech = "Noun",
            definitions = listOf(
                Definition("A sample word for letter {l}.", "This is a {l}Sample.")
            ),
            collocations = listOf("good {l}Sample"),
            idioms = listOf(),
            formalUsage = "This {l}Sample is formal.",
            informalUsage = "This {l}Sample is informal.",
            slangUsage = "slang",
            memoryHook = "hook",
            physicalAction = "action",
            mastery = MasteryExercise(
                fillInTheBlank = "This is a _____.",
                answer = "{l}Sample",
                paraphraseChallenge = "Explain {l}."
            ),
            category = "Casual"
        )""")

print(",".join(words))
