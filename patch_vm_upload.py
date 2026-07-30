import re

path = "app/src/main/java/com/example/viewmodel/LexiViewModel.kt"
with open(path, "r") as f:
    content = f.read()

import_str = """import java.util.Calendar
import com.example.data.WordObject"""

new_import_str = """import java.util.Calendar
import com.example.data.WordObject
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import android.net.Uri"""

content = content.replace(import_str, new_import_str)

upload_func = """    fun addWord(word: WordObject) {
        viewModelScope.launch {
            repository.insertWord(word)
        }
    }
    
    fun uploadRecording(file: File, sentence: String, score: Int) {
        val user = auth.currentUser ?: return
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child("recordings/${user.uid}/${System.currentTimeMillis()}.3gp")
        
        ref.putFile(Uri.fromFile(file)).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val recordingData = mapOf(
                    "url" to uri.toString(),
                    "sentence" to sentence,
                    "score" to score,
                    "timestamp" to com.google.firebase.Timestamp.now()
                )
                db.collection("users").document(user.uid)
                  .collection("recordings").add(recordingData)
            }
        }
    }"""

content = content.replace("    fun addWord(word: WordObject) {\n        viewModelScope.launch {\n            repository.insertWord(word)\n        }\n    }", upload_func)

with open(path, "w") as f:
    f.write(content)
