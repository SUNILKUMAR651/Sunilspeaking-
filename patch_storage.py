import re

path = "gradle/libs.versions.toml"
with open(path, "r") as f:
    content = f.read()

if "firebase-storage" not in content:
    content = content.replace('firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }', 'firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }\nfirebase-storage = { group = "com.google.firebase", name = "firebase-storage" }')
    with open(path, "w") as f:
        f.write(content)

path2 = "app/build.gradle.kts"
with open(path2, "r") as f:
    content2 = f.read()

if "firebase.storage" not in content2:
    content2 = content2.replace('implementation(libs.firebase.auth)', 'implementation(libs.firebase.auth)\n  implementation(libs.firebase.storage)')
    with open(path2, "w") as f:
        f.write(content2)
