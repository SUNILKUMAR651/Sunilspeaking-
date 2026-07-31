import re
with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "r") as f:
    content = f.read()

# Replace API key
content = re.sub(r'private val API_KEY = "[^"]+"', 'private val API_KEY = "c6fe65b595354573b51a572583381ada"', content)

# Remove the reference_id since it was actually an API key mistaken for a voice ID.
# Or replace it with actual valid reference IDs if available, or just omit it completely to use the default voice.
bad_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                    if (isFemale) {
                        put("reference_id", "c6fe65b595354573b51a572583381ada")
                    } else {
                        // Using a generic male voice ID if available or omitting.
                        put("reference_id", "85b2e59a4c804f5e8b0933f7c327fc25") // Example standard male voice if needed, but if it fails it falls back.
                    }
                }"""

good_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                    // Removed invalid reference_id, will use default voice
                }"""

if bad_json in content:
    content = content.replace(bad_json, good_json)

with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "w") as f:
    f.write(content)
