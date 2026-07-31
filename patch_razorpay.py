import re
path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

bad = """            options.put("amount", amount * 100) // in paise
            options.put("theme.color", "#6B4EE6")
            checkout.open(this, options)"""
good = """            options.put("amount", amount * 100) // in paise
            options.put("theme.color", "#6B4EE6")
            
            val prefill = JSONObject()
            prefill.put("email", "test@example.com")
            prefill.put("contact", "9999999999")
            options.put("prefill", prefill)
            
            checkout.open(this, options)"""

content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
