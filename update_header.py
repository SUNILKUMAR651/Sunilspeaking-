path = "app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt"
with open(path, 'r') as f:
    content = f.read()

if "VolumeOff" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Close", "import androidx.compose.material.icons.filled.Close\nimport androidx.compose.material.icons.filled.VolumeUp\nimport androidx.compose.material.icons.filled.VolumeOff")

old_header = """            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFF58CC02),
                trackColor = Color(0xFFE5E5E5),
                strokeCap = StrokeCap.Round
            )
        }"""

new_header = """            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFF58CC02),
                trackColor = Color(0xFFE5E5E5),
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { 
                viewModel.updateProfile(userProfile.copy(audioEnabled = !userProfile.audioEnabled)) 
            }) {
                Icon(
                    imageVector = if (userProfile.audioEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                    contentDescription = if (userProfile.audioEnabled) "Mute Audio" else "Unmute Audio",
                    tint = Color.Gray
                )
            }
        }"""

content = content.replace(old_header, new_header)

with open(path, 'w') as f:
    f.write(content)

