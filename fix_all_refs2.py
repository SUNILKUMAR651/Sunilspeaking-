import os, re

dir_paths = ["app/src/main/java/com/example/ui/screens/", "app/src/main/java/com/example/ui/speaking/"]
for dp in dir_paths:
    for f in os.listdir(dp):
        if not f.endswith('.kt'): continue
        path = os.path.join(dp, f)
        with open(path, 'r') as file:
            content = file.read()
            
        content = content.replace("val profile by viewModel.userProfile", "val userProfile by viewModel.userProfile")
        content = content.replace("profile.name", "userProfile.name")
        content = content.replace("profile.useFemaleVoice", "userProfile.useFemaleVoice")
        content = content.replace("profile.initials", "userProfile.initials")
        content = content.replace("profile.dayStreak", "userProfile.dayStreak")
        content = content.replace("profile.totalXp", "userProfile.totalXp")
        content = content.replace("profile.level", "userProfile.level")
        content = content.replace("profile.isAdmin", "userProfile.isAdmin")
        content = content.replace("profile.isPremium", "userProfile.isPremium")
        content = content.replace("profile.unlockedCertificates", "userProfile.unlockedCertificates")
        content = content.replace("profile.vocabularyProgress", "userProfile.vocabularyProgress")
        content = content.replace("profile.grammarProgress", "userProfile.grammarProgress")
        content = content.replace("profile.speakingProgress", "userProfile.speakingProgress")
        content = content.replace("profile.listeningProgress", "userProfile.listeningProgress")
        content = content.replace("profile.certificationProgress", "userProfile.certificationProgress")

        # Also fix the bad injection in InteractivePracticeScreen & ActiveLessonScreen
        # where it put it outside the composable.
        content = content.replace(")\n    val userProfile by viewModel.userProfile.collectAsState() {\n", ") {\n    val userProfile by viewModel.userProfile.collectAsState()\n")
        
        with open(path, 'w') as file:
            file.write(content)
