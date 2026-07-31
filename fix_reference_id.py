import re
with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "r") as f:
    content = f.read()

bad_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                    // Removed invalid reference_id, will use default voice
                }"""

good_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                    put("reference_id", "c6fe65b595354573b51a572583381ada") // The user's specific voice ID
                }"""

if bad_json in content:
    content = content.replace(bad_json, good_json)

with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "w") as f:
    f.write(content)
