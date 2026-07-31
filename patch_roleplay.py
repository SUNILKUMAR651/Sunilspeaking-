import re
path = "app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("import kotlinx.coroutines.tasks.await", "import kotlinx.coroutines.tasks.await\nimport com.example.utils.retryWithBackoff")

content = content.replace("val doc = db.collection(\"users\").document(userId).collection(\"roleplays\").document(scenario.id).get().await()", "val doc = retryWithBackoff { db.collection(\"users\").document(userId).collection(\"roleplays\").document(scenario.id).get().await() }")

content = content.replace("db.collection(\"users\").document(userId).collection(\"roleplays\").document(scenario.id)\n                        .set(mapOf(\"messages\" to messagesList), SetOptions.merge())", "retryWithBackoff { db.collection(\"users\").document(userId).collection(\"roleplays\").document(scenario.id)\n                        .set(mapOf(\"messages\" to messagesList), SetOptions.merge()).await() }")


with open(path, "w") as f:
    f.write(content)
