import re

path = "app/src/main/java/com/example/data/DataStore.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace('"Business English"', '"Business"')
content = content.replace('"Medical English"', '"Medical"')

with open(path, "w") as f:
    f.write(content)
