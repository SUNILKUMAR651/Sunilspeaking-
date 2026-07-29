package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class Definition(
    val meaning: String,
    val example: String
)

@Serializable
data class MasteryExercise(
    val fillInTheBlank: String,
    val answer: String,
    val paraphraseChallenge: String
)

@Serializable
@Entity(tableName = "words")
data class WordObject(
    @PrimaryKey
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val definitions: List<Definition>,
    val collocations: List<String>,
    val idioms: List<String>,
    val formalUsage: String,
    val informalUsage: String,
    val slangUsage: String,
    val memoryHook: String,
    val physicalAction: String,
    val mastery: MasteryExercise,
    val category: String,
    val startingLetter: String = word.first().uppercaseChar().toString(),
    val nextReviewDate: Long = 0L,
    val interval: Int = 0,
    val easinessFactor: Float = 2.5f,
    val repetitions: Int = 0
)
