import re
path = "app/src/main/java/com/example/data/repository/LexiRepository.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("import kotlinx.coroutines.tasks.await", "import kotlinx.coroutines.tasks.await\nimport com.example.utils.retryWithBackoff")

content = content.replace("FirebaseFirestore.getInstance().collection(\"words\").document(word.word).set(word).await()", "retryWithBackoff { FirebaseFirestore.getInstance().collection(\"words\").document(word.word).set(word).await() }")
content = content.replace("FirebaseFirestore.getInstance().collection(\"users\")\n                .orderBy(\"totalXp\", com.google.firebase.firestore.Query.Direction.DESCENDING)\n                .limit(limit)\n                .get()\n                .await()", "retryWithBackoff { FirebaseFirestore.getInstance().collection(\"users\")\n                .orderBy(\"totalXp\", com.google.firebase.firestore.Query.Direction.DESCENDING)\n                .limit(limit)\n                .get()\n                .await() }")
content = content.replace("FirebaseFirestore.getInstance().collection(\"users\").document(userId).get().await()", "retryWithBackoff { FirebaseFirestore.getInstance().collection(\"users\").document(userId).get().await() }")
content = content.replace("FirebaseFirestore.getInstance().collection(\"users\").document(userId).set(newProfile).await()", "retryWithBackoff { FirebaseFirestore.getInstance().collection(\"users\").document(userId).set(newProfile).await() }")
content = content.replace("FirebaseFirestore.getInstance().collection(\"users\").document(profile.id).set(profile).await()", "retryWithBackoff { FirebaseFirestore.getInstance().collection(\"users\").document(profile.id).set(profile).await() }")

with open(path, "w") as f:
    f.write(content)
