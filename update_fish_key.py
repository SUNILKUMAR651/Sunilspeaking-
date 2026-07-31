import re
with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "r") as f:
    content = f.read()

# Replace API key with the user's provided fish audio key
content = re.sub(r'private val API_KEY = "[^"]+"', 'private val API_KEY = "c6fe65b595354573b51a572583381ada"', content)

with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "w") as f:
    f.write(content)
