path = "app/src/main/java/com/example/ui/screens/PremiumSubscriptionScreen.kt"
with open(path, "r") as f:
    content = f.read()

bad_string = '"Business "Business & Professional English Certification" Professional Language Certification"'
content = content.replace(bad_string, '"Business & Professional Language Certification"')

with open(path, "w") as f:
    f.write(content)
