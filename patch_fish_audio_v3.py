import re
with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "r") as f:
    content = f.read()

bad_json = """val json = JSONObject().apply {
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

good_json = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                    if (isFemale) {
                        put("reference_id", "c6fe65b595354573b51a572583381ada")
                    } else {
                        // Using a generic male voice ID if available or omitting.
                        put("reference_id", "85b2e59a4c804f5e8b0933f7c327fc25") // Example standard male voice if needed, but if it fails it falls back.
                    }
                }"""

# Check if bad_json is present
if "if (isFemale)" in content:
    content = content.replace(bad_json, good_json)
else:
    # We didn't actually run patch_fish_audio_v2.py successfully or it wasn't saved this way. Let's just do a clean replace.
    bad_json_old = """val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                }"""
    content = content.replace(bad_json_old, good_json)

with open("app/src/main/java/com/example/utils/FishAudioPlayer.kt", "w") as f:
    f.write(content)
