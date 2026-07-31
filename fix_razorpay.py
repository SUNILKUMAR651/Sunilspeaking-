import re
path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

bad = "val apiKey = BuildConfig.RAZORPAY_API_KEY"
good = "val apiKey = \"rzp_test_TJFnozzpPbub2B\""

content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
