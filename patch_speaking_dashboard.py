import re
path = "app/src/main/java/com/example/ui/screens/SpeakingPracticeDashboard.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("Job Interview English Practice", "Job Interview Practice")
content = content.replace("UK Visa English Requirement Prep", "Visa Language Requirement Prep")
content = content.replace("MBA Interview English Practice", "MBA Interview Practice")

with open(path, "w") as f:
    f.write(content)

path2 = "app/src/main/java/com/example/ui/screens/SpeakingTopicDetailScreen.kt"
with open(path2, "r") as f:
    content2 = f.read()
content2 = content2.replace('?: "Job Interview English Practice"', '?: "Job Interview Practice"')
with open(path2, "w") as f:
    f.write(content2)

