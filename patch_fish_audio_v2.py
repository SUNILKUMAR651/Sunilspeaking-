import re
with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "r") as f:
    content = f.read()

bad_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                }"""

good_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                    if (isFemale) {
                        put("reference_id", "c6fe65b595354573b51a572583381ada")
                    } else {
                        // Use a default male reference ID if available or just omit. 
                        // We will omit for male if we don't have one, or set a male voice ID.
                        // Let's omit for male so it falls back to standard voice, or use a known one.
                    }
                }"""

content = content.replace(bad_json, good_json)

with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "w") as f:
    f.write(content)
