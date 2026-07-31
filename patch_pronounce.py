import re
path = "app/src/main/java/com/example/ui/screens/PronunciationHistoryScreen.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("import kotlinx.coroutines.tasks.await", "import kotlinx.coroutines.tasks.await\nimport com.example.utils.retryWithBackoff")

content = content.replace("db.collection(\"users\").document(user.uid).collection(\"pronunciation\")\n                    .orderBy(\"timestamp\", com.google.firebase.firestore.Query.Direction.DESCENDING)\n                    .get()\n                    .await()", "retryWithBackoff { db.collection(\"users\").document(user.uid).collection(\"pronunciation\")\n                    .orderBy(\"timestamp\", com.google.firebase.firestore.Query.Direction.DESCENDING)\n                    .get()\n                    .await() }")


with open(path, "w") as f:
    f.write(content)
