import re

path = "app/src/main/java/com/example/ui/screens/FluencyCertificationScreen.kt"
with open(path, 'r') as f:
    content = f.read()

if "import com.example.ui.components.ConfettiAnimation" not in content:
    content = content.replace("import androidx.compose.ui.Alignment", "import androidx.compose.ui.Alignment\nimport com.example.ui.components.ConfettiAnimation\nimport androidx.compose.runtime.getValue\nimport androidx.compose.runtime.setValue\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember\nimport androidx.compose.foundation.layout.Box")

# Inject showConfetti state
content = content.replace("val userProfile by viewModel.userProfile.collectAsState()\n    \n    val certificates", "val userProfile by viewModel.userProfile.collectAsState()\n    var showConfetti by remember { mutableStateOf(false) }\n    val certificates")

# Inside onClaim
old_claim = """                    onClaim = {
                        val newUnlocked = userProfile.unlockedCertificates + cert.title
                        viewModel.updateProfile(userProfile.copy(
                            unlockedCertificates = newUnlocked,
                            level = cert.title
                        ))
                    }"""

new_claim = """                    onClaim = {
                        val newUnlocked = userProfile.unlockedCertificates + cert.title
                        viewModel.updateProfile(userProfile.copy(
                            unlockedCertificates = newUnlocked,
                            level = cert.title
                        ))
                        showConfetti = true
                    }"""

content = content.replace(old_claim, new_claim)

# Add ConfettiAnimation inside the main Scaffold but over everything.
# Scaffold content ends with:
old_end = """                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}"""

new_end = """                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (showConfetti) {
            ConfettiAnimation(onFinished = { showConfetti = false })
        }
    }
}"""

content = content.replace(old_end, new_end)

with open(path, 'w') as f:
    f.write(content)
