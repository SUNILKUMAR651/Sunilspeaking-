import os, re

dir_path = "app/src/main/java/com/example/ui/screens/"
for f in os.listdir(dir_path):
    if not f.endswith('.kt'): continue
    
    path = os.path.join(dir_path, f)
    with open(path, 'r') as file:
        content = file.read()
        
    # Standardize to userProfile instead of profile
    content = content.replace("val profile by viewModel.userProfile", "val userProfile by viewModel.userProfile")
    content = content.replace("profile.name", "userProfile.name")
    content = content.replace("profile.useFemaleVoice", "userProfile.useFemaleVoice")
    content = content.replace("profile.initials", "userProfile.initials")
    content = content.replace("profile.dayStreak", "userProfile.dayStreak")
    content = content.replace("profile.totalXp", "userProfile.totalXp")

    with open(path, 'w') as file:
        file.write(content)

# Same for speaking dir
dir_path_speak = "app/src/main/java/com/example/ui/speaking/"
for f in os.listdir(dir_path_speak):
    if not f.endswith('.kt'): continue
    path = os.path.join(dir_path_speak, f)
    with open(path, 'r') as file:
        content = file.read()
        
    content = content.replace("val profile by viewModel.userProfile", "val userProfile by viewModel.userProfile")
    content = content.replace("profile.name", "userProfile.name")
    content = content.replace("profile.useFemaleVoice", "userProfile.useFemaleVoice")
    with open(path, 'w') as file:
        file.write(content)

