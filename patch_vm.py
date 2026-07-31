import re
path = "app/src/main/java/com/example/viewmodel/LexiViewModel.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("import kotlinx.coroutines.tasks.await", "import kotlinx.coroutines.tasks.await\nimport com.example.utils.retryWithBackoff")
content = content.replace("val result = auth.signInWithEmailAndPassword(email, password).await()", "val result = retryWithBackoff { auth.signInWithEmailAndPassword(email, password).await() }")
content = content.replace("val result = auth.createUserWithEmailAndPassword(email, password).await()", "val result = retryWithBackoff { auth.createUserWithEmailAndPassword(email, password).await() }")

# wait, there's a FirebaseStorage upload:
# val ref = storage.reference.child("avatars/${user.uid}.jpg")
# ref.putFile(uri).await()
# val downloadUrl = ref.downloadUrl.await()

content = content.replace("ref.putFile(uri).await()", "retryWithBackoff { ref.putFile(uri).await() }")
content = content.replace("val downloadUrl = ref.downloadUrl.await()", "val downloadUrl = retryWithBackoff { ref.downloadUrl.await() }")

# val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
# db.collection("users").document(user.uid).update("avatarUrl", downloadUrl.toString()).await()
content = content.replace("db.collection(\"users\").document(user.uid).update(\"avatarUrl\", downloadUrl.toString()).await()", "retryWithBackoff { db.collection(\"users\").document(user.uid).update(\"avatarUrl\", downloadUrl.toString()).await() }")

with open(path, "w") as f:
    f.write(content)
