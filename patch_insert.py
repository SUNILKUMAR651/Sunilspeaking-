import re
path = "app/src/main/java/com/example/data/repository/LexiRepository.kt"
with open(path, "r") as f:
    content = f.read()

bad_block = """        try { 
             retryWithBackoff { FirebaseFirestore.getInstance().collection("words").document(word.word).set(word).await() } 
         } catch (e: Exception) { 
             e.printStackTrace()
            throw e
        }"""

good_block = """        try { 
             retryWithBackoff { FirebaseFirestore.getInstance().collection("words").document(word.word).set(word).await() } 
         } catch (e: Exception) { 
             e.printStackTrace()
        }"""

content = content.replace(bad_block, good_block)

with open(path, "w") as f:
    f.write(content)
