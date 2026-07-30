import re
path = "app/src/main/java/com/example/ui/screens/PremiumSubscriptionScreen.kt"
with open(path, "r") as f:
    lines = f.readlines()
with open(path, "w") as f:
    for line in lines:
        if "Professional Language Certification" in line:
            f.write('            PremiumFeatureRow("Business & Professional Language Certification")\n')
        else:
            f.write(line)
