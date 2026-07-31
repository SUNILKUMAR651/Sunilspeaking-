import re
with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "r") as f:
    content = f.read()

content = re.sub(r'private val API_KEY = "[^"]+"', 'private val API_KEY = "cb4905e8b23a48b5b1ee3946315e2403"', content)

with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "w") as f:
    f.write(content)
