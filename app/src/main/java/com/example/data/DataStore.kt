package com.example.data

object LexiconDatabase {
    val words = listOf(
        WordObject(
            word = "Articulate",
            phonetic = "/ɑːrˈtɪkjələt/",
            partOfSpeech = "Adjective / Verb",
            definitions = listOf(
                Definition("Able to express thoughts and feelings easily and clearly.", "She is an articulate speaker who can explain complex concepts to clients."),
                Definition("To pronounce words distinctly.", "He articulated each syllable with precision during the presentation.")
            ),
            collocations = listOf("highly articulate", "articulate a vision", "articulate one's thoughts", "articulate speaker", "clear and articulate"),
            idioms = listOf("put into words", "spell it out", "make oneself clear", "have a way with words", "find your voice"),
            formalUsage = "The CEO articulated the new strategic direction to the board.",
            informalUsage = "She's really good at explaining things clearly.",
            slangUsage = "Smooth talker",
            memoryHook = "Think of 'Art' - someone who paints a clear picture with words.",
            physicalAction = "Moving hands outward smoothly from the chest, as if presenting a gift.",
            mastery = MasteryExercise(
                fillInTheBlank = "In order to secure funding, we must clearly _____ our business model to the investors.",
                answer = "articulate",
                paraphraseChallenge = "Explain exactly what we are trying to achieve in this project."
            ),
            category = "Business"
        ),
        WordObject(
            word = "Adept",
            phonetic = "/əˈdept/",
            partOfSpeech = "Adjective",
            definitions = listOf(
                Definition("Very skilled or proficient at something.", "He is adept at managing cross-functional teams in high-stress environments.")
            ),
            collocations = listOf("adept at", "technically adept", "prove adept", "highly adept", "adept handling"),
            idioms = listOf("know the ropes", "old hand", "have down to a science", "master of", "have a knack for"),
            formalUsage = "Our firm is adept at navigating complex regulatory frameworks.",
            informalUsage = "He's super good at putting out fires at work.",
            slangUsage = "Pro, goat",
            memoryHook = "ADAPT - Someone adept can easily adapt and solve problems.",
            physicalAction = "A sharp, confident nod with a subtle smile of competence.",
            mastery = MasteryExercise(
                fillInTheBlank = "She is particularly _____ at negotiating favorable terms with suppliers.",
                answer = "adept",
                paraphraseChallenge = "He is really good at organizing these kinds of events."
            ),
            category = "Business"
        ),
        WordObject(
            word = "Benchmark",
            phonetic = "/ˈbentʃmɑːrk/",
            partOfSpeech = "Noun / Verb",
            definitions = listOf(
                Definition("A standard or point of reference against which things may be compared or assessed.", "Our latest software release sets a new benchmark for industry security."),
                Definition("Evaluate or check (something) by comparison with a standard.", "We need to benchmark our performance against our top competitors.")
            ),
            collocations = listOf("set a benchmark", "industry benchmark", "benchmark against", "global benchmark", "performance benchmark"),
            idioms = listOf("set the bar", "gold standard", "measure up", "par for the course", "rule of thumb"),
            formalUsage = "The financial metrics achieved this quarter will serve as a benchmark for future projections.",
            informalUsage = "Let's use this as our standard to see if we're doing okay.",
            slangUsage = "The goat standard",
            memoryHook = "A 'Bench' with a 'Mark' on it showing exactly how high to jump.",
            physicalAction = "Drawing a horizontal line in the air with a flat hand.",
            mastery = MasteryExercise(
                fillInTheBlank = "The new customer satisfaction score is the new _____ by which we will measure all future campaigns.",
                answer = "benchmark",
                paraphraseChallenge = "We need a standard to compare our results to."
            ),
            category = "Academic"
        ),
        WordObject(
            word = "Bolster",
            phonetic = "/ˈboʊlstər/",
            partOfSpeech = "Verb",
            definitions = listOf(
                Definition("Support or strengthen; prop up.", "The positive Q3 earnings report will bolster investor confidence.")
            ),
            collocations = listOf("bolster confidence", "bolster the argument", "bolster support", "bolster the economy", "significantly bolster"),
            idioms = listOf("shore up", "give a boost", "prop up", "back up", "beef up"),
            formalUsage = "We have implemented new compliance measures to bolster data security.",
            informalUsage = "This should really help our case with the boss.",
            slangUsage = "Hype up",
            memoryHook = "Think of a 'Bolster' pillow that supports your back.",
            physicalAction = "Pushing two hands upwards as if supporting a heavy weight.",
            mastery = MasteryExercise(
                fillInTheBlank = "We need additional market research to _____ our pitch to the venture capitalists.",
                answer = "bolster",
                paraphraseChallenge = "We need to make our main argument stronger before the presentation."
            ),
            category = "Business"
        ),
        WordObject(
            word = "Catalyst",
            phonetic = "/ˈkætəlɪst/",
            partOfSpeech = "Noun",
            definitions = listOf(
                Definition("A person or thing that precipitates an event.", "The new CEO acted as a catalyst for a massive corporate restructuring."),
                Definition("A substance that increases the rate of a chemical reaction.", "Enzymes act as a biological catalyst.")
            ),
            collocations = listOf("act as a catalyst", "major catalyst", "catalyst for change", "serve as a catalyst", "provide a catalyst"),
            idioms = listOf("spark off", "get the ball rolling", "light a fire under", "set in motion", "kick start"),
            formalUsage = "Technological innovation has been the primary catalyst for economic expansion in this sector.",
            informalUsage = "He's the one who finally made things happen.",
            slangUsage = "Game changer",
            memoryHook = "Cat + List - The cat knocked over the list, starting a chain reaction of chaos.",
            physicalAction = "Snapping fingers sharply to indicate a sudden spark or change.",
            mastery = MasteryExercise(
                fillInTheBlank = "The merger could be the _____ we need to dominate the European market.",
                answer = "catalyst",
                paraphraseChallenge = "This new software will be the thing that finally speeds up our workflow."
            ),
            category = "Scientific"
        ),
        WordObject(
            word = "Consensus",
            phonetic = "/kənˈsensəs/",
            partOfSpeech = "Noun",
            definitions = listOf(
                Definition("A general agreement.", "After hours of deliberation, the board finally reached a consensus.")
            ),
            collocations = listOf("reach a consensus", "build consensus", "general consensus", "lack of consensus", "broad consensus"),
            idioms = listOf("on the same page", "see eye to eye", "meet in the middle", "find common ground", "of one mind"),
            formalUsage = "There is a growing consensus among analysts that the market is heading toward a correction.",
            informalUsage = "We all finally agreed on what to do.",
            slangUsage = "We're solid",
            memoryHook = "CON (together) + SENSE = Everyone making sense together.",
            physicalAction = "Bringing both hands together with interlocking fingers.",
            mastery = MasteryExercise(
                fillInTheBlank = "We cannot move forward with the product launch until we reach a _____ across all departments.",
                answer = "consensus",
                paraphraseChallenge = "Everyone needs to agree on this before we make a decision."
            ),
            category = "Business"
        ),
        WordObject(
            word = "Delegate",
            phonetic = "/ˈdelɪɡət/ (Noun), /ˈdelɪɡeɪt/ (Verb)",
            partOfSpeech = "Verb / Noun",
            definitions = listOf(
                Definition("Entrust (a task or responsibility) to another person.", "A good manager must know how to delegate tasks effectively."),
                Definition("A person sent or authorized to represent others.", "The union delegates negotiated a new contract.")
            ),
            collocations = listOf("delegate authority", "delegate tasks", "delegate effectively", "delegate responsibility", "send a delegate"),
            idioms = listOf("pass the baton", "hand over the reins", "share the load", "put someone in charge", "farm out"),
            formalUsage = "I will delegate the execution of the marketing strategy to the regional directors.",
            informalUsage = "I'm passing this job over to you.",
            slangUsage = "Handing off",
            memoryHook = "DEL (delete) + GATE - Delete the task from your own plate and pass it through the gate to someone else.",
            physicalAction = "A sweeping hand motion outwards, as if handing an invisible file to someone else.",
            mastery = MasteryExercise(
                fillInTheBlank = "To avoid burnout, you must learn to _____ routine tasks to your junior staff.",
                answer = "delegate",
                paraphraseChallenge = "You should give some of your work to other team members."
            ),
            category = "Business"
        ),
        WordObject(
            word = "Diligent",
            phonetic = "/ˈdɪlɪdʒənt/",
            partOfSpeech = "Adjective",
            definitions = listOf(
                Definition("Having or showing care and conscientiousness in one's work or duties.", "After a diligent search, the auditors uncovered the accounting error.")
            ),
            collocations = listOf("diligent effort", "diligent work", "diligent student", "remain diligent", "highly diligent"),
            idioms = listOf("leave no stone unturned", "go the extra mile", "keep your nose to the grindstone", "dot the i's and cross the t's", "sweat the details"),
            formalUsage = "We appreciate the diligent efforts of the committee in preparing this comprehensive report.",
            informalUsage = "She works really hard and checks everything twice.",
            slangUsage = "Grinding",
            memoryHook = "DILI (Daily) + GENT (Gentleman) - A gentleman who works hard daily.",
            physicalAction = "Mimicking writing intently while nodding with focus.",
            mastery = MasteryExercise(
                fillInTheBlank = "The team was commended for their _____ preparation ahead of the high-stakes negotiations.",
                answer = "diligent",
                paraphraseChallenge = "He works very hard and pays close attention to everything."
            ),
            category = "Casual"
        ),
        WordObject(
            word = "Eloquent",
            phonetic = "/ˈeləkwənt/",
            partOfSpeech = "Adjective",
            definitions = listOf(
                Definition("Fluent or persuasive in speaking or writing.", "She delivered an eloquent defense of the new environmental policies.")
            ),
            collocations = listOf("eloquent speaker", "eloquent speech", "eloquent testimony", "eloquently state", "highly eloquent"),
            idioms = listOf("have a silver tongue", "smooth talker", "gift of the gab", "speak volumes", "wax lyrical"),
            formalUsage = "His eloquent address successfully persuaded the shareholders to approve the merger.",
            informalUsage = "He speaks really beautifully and convinces people easily.",
            slangUsage = "Spitting bars",
            memoryHook = "ELEGANT + QUOTE = Someone who says elegant quotes.",
            physicalAction = "A graceful, sweeping hand gesture indicating smooth, flowing speech.",
            mastery = MasteryExercise(
                fillInTheBlank = "The lawyer's _____ closing statement left the jury in silent contemplation.",
                answer = "eloquent",
                paraphraseChallenge = "She gave a very persuasive and well-spoken presentation."
            ),
            category = "Emotional"
        ),
        WordObject(
            word = "Expedite",
            phonetic = "/ˈekspədaɪt/",
            partOfSpeech = "Verb",
            definitions = listOf(
                Definition("Make (an action or process) happen sooner or be accomplished more quickly.", "We need to expedite the shipping process to meet the deadline.")
            ),
            collocations = listOf("expedite the process", "expedite delivery", "expedite matters", "help expedite", "seek to expedite"),
            idioms = listOf("fast-track", "speed things up", "put a rush on", "step on it", "get a move on"),
            formalUsage = "I have requested the logistics department to expedite the delivery of the essential components.",
            informalUsage = "Can we hurry this up?",
            slangUsage = "Rush it",
            memoryHook = "EX (Extra) + PED (Foot) = Add an extra foot to run faster.",
            physicalAction = "A quick, forward pushing motion with the dominant hand.",
            mastery = MasteryExercise(
                fillInTheBlank = "Please contact the vendor and see if they can _____ the processing of our order.",
                answer = "expedite",
                paraphraseChallenge = "We need to figure out how to make this project finish faster."
            ),
            category = "Business"
        ),
        WordObject(
            word = "Synergy",
            phonetic = "/ˈsɪnərdʒi/",
            partOfSpeech = "Noun",
            definitions = listOf(
                Definition("The interaction or cooperation of two or more organizations, substances, or other agents to produce a combined effect greater than the sum of their separate effects.", "The synergy between the two departments resulted in a highly successful campaign.")
            ),
            collocations = listOf("create synergy", "team synergy", "business synergy", "positive synergy"),
            idioms = listOf("two heads are better than one", "team effort", "pull together"),
            formalUsage = "The merger is expected to create significant synergy, enhancing overall operational efficiency.",
            informalUsage = "We work really well together and get more done.",
            slangUsage = "Vibing together",
            memoryHook = "SYN (together) + ERGY (energy) = Working together creates more energy.",
            physicalAction = "Bringing both hands together and interlocking the fingers.",
            mastery = MasteryExercise(
                fillInTheBlank = "By merging our teams, we hope to achieve a level of _____ that boosts our total output.",
                answer = "synergy",
                paraphraseChallenge = "We can accomplish much more if we combine our efforts instead of working separately."
            ),
            category = "Business English"
        ),
        WordObject(
            word = "Agile",
            phonetic = "/ˈædʒaɪl/",
            partOfSpeech = "Adjective",
            definitions = listOf(
                Definition("Able to move quickly and easily.", "Ruth was as agile as a monkey."),
                Definition("Relating to a method of project management that is characterized by the division of tasks into short phases of work.", "We use agile methods for software development.")
            ),
            collocations = listOf("agile methodology", "agile development", "agile approach", "agile team"),
            idioms = listOf("quick on your feet", "nimble", "light on your feet"),
            formalUsage = "Our organization must remain agile to adapt to rapidly changing market conditions.",
            informalUsage = "We need to be flexible and move fast.",
            slangUsage = "Quick to pivot",
            memoryHook = "Think of a gymnast who is very flexible and quick.",
            physicalAction = "A quick, dodging movement to the side.",
            mastery = MasteryExercise(
                fillInTheBlank = "The software development team adopted an _____ methodology to respond faster to client feedback.",
                answer = "agile",
                paraphraseChallenge = "We need a flexible approach that allows us to change direction quickly."
            ),
            category = "IT & Tech"
        ),
        WordObject(
            word = "Itinerary",
            phonetic = "/aɪˈtɪnəreri/",
            partOfSpeech = "Noun",
            definitions = listOf(
                Definition("A planned route or journey.", "We planned our itinerary to include the best museums in Paris.")
            ),
            collocations = listOf("travel itinerary", "plan an itinerary", "detailed itinerary", "follow an itinerary"),
            idioms = listOf("travel plan", "route map"),
            formalUsage = "Please review the attached itinerary for your upcoming business trip to Tokyo.",
            informalUsage = "Here's our schedule for the trip.",
            slangUsage = "Travel plans",
            memoryHook = "IT IN (it in) ERARY (a diary) = Put it in a diary for travel.",
            physicalAction = "Tracing a route on a map with a finger.",
            mastery = MasteryExercise(
                fillInTheBlank = "The travel agent sent us the final _____ for our vacation.",
                answer = "itinerary",
                paraphraseChallenge = "Here is the list of places we will visit and the schedule for our trip."
            ),
            category = "Travel & Tourism"
        ),
        WordObject(
            word = "Prognosis",
            phonetic = "/prɑːɡˈnoʊsɪs/",
            partOfSpeech = "Noun",
            definitions = listOf(
                Definition("The likely course of a disease or ailment.", "The disease has a poor prognosis."),
                Definition("A forecast of the likely outcome of a situation.", "It is very difficult to make an accurate prognosis.")
            ),
            collocations = listOf("good prognosis", "poor prognosis", "give a prognosis", "favorable prognosis"),
            idioms = listOf("future outlook", "prediction"),
            formalUsage = "Despite the severity of the injury, the doctor gave a very optimistic prognosis.",
            informalUsage = "The doctor said I'll get better soon.",
            slangUsage = "The outlook",
            memoryHook = "PRO (before) + GNOSIS (knowledge) = Knowing beforehand how it will turn out.",
            physicalAction = "Looking forward with a hand shading the eyes.",
            mastery = MasteryExercise(
                fillInTheBlank = "After the successful surgery, the patient's _____ for a full recovery is excellent.",
                answer = "prognosis",
                paraphraseChallenge = "What is the expected outcome for this patient's condition?"
            ),
            category = "Medical English"
        ),
        WordObject(
            word = "Leverage",
            phonetic = "/ˈlevərɪdʒ/",
            partOfSpeech = "Verb / Noun",
            definitions = listOf(
                Definition("Use (something) to maximum advantage.", "The organization needs to leverage its key resources."),
                Definition("The power to influence a person or situation.", "The union's size gave it leverage in the negotiations.")
            ),
            collocations = listOf("leverage resources", "leverage technology", "gain leverage", "have leverage"),
            idioms = listOf("make the most of", "capitalize on", "take advantage of"),
            formalUsage = "We must leverage our existing technology infrastructure to expand into new markets.",
            informalUsage = "Let's use what we already have to do better.",
            slangUsage = "Use it",
            memoryHook = "Like a lever that helps you lift heavy things, it helps you get more done.",
            physicalAction = "Pushing down on an imaginary lever.",
            mastery = MasteryExercise(
                fillInTheBlank = "The company plans to _____ its strong brand recognition to launch the new product line.",
                answer = "leverage",
                paraphraseChallenge = "We should use our current advantages to get better results."
            ),
            category = "Job Interview"
        ),
        WordObject(
            word = "Hello",
            phonetic = "/həˈloʊ/",
            partOfSpeech = "Noun / Interjection",
            definitions = listOf(
                Definition("Used as a greeting or to begin a phone conversation.", "Hello there!")
            ),
            collocations = listOf("say hello", "a warm hello", "hello everyone"),
            idioms = listOf("hi", "greetings"),
            formalUsage = "Hello, my name is John Doe, and I am the new regional manager.",
            informalUsage = "Hi, how are you?",
            slangUsage = "Hey, Yo",
            memoryHook = "The most basic greeting.",
            physicalAction = "Waving a hand.",
            mastery = MasteryExercise(
                fillInTheBlank = "Whenever you meet someone new, you should always say _____.",
                answer = "hello",
                paraphraseChallenge = "Greet the person."
            ),
            category = "Basic Vocab"
        )
    )
}
