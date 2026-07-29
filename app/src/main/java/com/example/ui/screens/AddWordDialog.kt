package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.WordObject

import com.example.viewmodel.LexiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordDialog(
    viewModel: LexiViewModel,
    onDismiss: () -> Unit
) {
    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(viewModel.categories.first()) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Word") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = { Text("Word") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Meaning") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text("Example Sentence") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        readOnly = false,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        viewModel.categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (word.isNotBlank() && meaning.isNotBlank()) {
                        val newWord = WordObject(
                            word = word.trim(),
                            phonetic = "",
                            partOfSpeech = "noun",
                            definitions = listOf(
                                com.example.data.Definition(
                                    meaning = meaning.trim(),
                                    example = example.trim()
                                )
                            ),
                            collocations = emptyList(),
                            idioms = emptyList(),
                            formalUsage = "",
                            informalUsage = "",
                            slangUsage = "",
                            memoryHook = "",
                            physicalAction = "",
                            mastery = com.example.data.MasteryExercise(
                                fillInTheBlank = "",
                                answer = "",
                                paraphraseChallenge = ""
                            ),
                            category = category
                        )
                        viewModel.addWord(newWord)
                        onDismiss()
                    }
                },
                enabled = word.isNotBlank() && meaning.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
