import re
with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "r") as f:
    content = f.read()

# Replace API key with the exact one they provided
content = re.sub(r'private val API_KEY = "[^"]+"', 'private val API_KEY = "c6fe65b595354573b51a572583381ada"', content)

# Remove the reference_id since it's the API key, not a voice ID
bad_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                    put("reference_id", "c6fe65b595354573b51a572583381ada") // The user's specific voice ID
                }"""

good_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                }"""
content = content.replace(bad_json, good_json)

with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "w") as f:
    f.write(content)
