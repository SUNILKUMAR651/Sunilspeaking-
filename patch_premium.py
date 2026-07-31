import re
path = "app/src/main/java/com/example/ui/screens/PremiumSubscriptionScreen.kt"
with open(path, "r") as f:
    content = f.read()

bad = "val activity = context as? MainActivity"
good = """var activityContext = context
                                while (activityContext is android.content.ContextWrapper) {
                                    if (activityContext is MainActivity) break
                                    activityContext = activityContext.baseContext
                                }
                                val activity = activityContext as? MainActivity"""

content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
