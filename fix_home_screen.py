path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, 'r') as f:
    content = f.read()

old_actions = """                // Quick Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GlassmorphicActionCard(
                            title = "Roleplay",
                            subtitle = "AI Voice",
                            icon = Icons.Filled.SmartToy,
                            accentColor = Color(0xFFB388FF),
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("ai_roleplay") }
                        
                        GlassmorphicActionCard(
                            title = "Speaking",
                            subtitle = "Pronunciation",
                            icon = Icons.Filled.Mic,
                            accentColor = Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f)
                        ) { onNavigate("speaking_practice") }
                    }
                }"""

new_actions = """                // Quick Actions
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GlassmorphicActionCard(
                                title = "Roleplay",
                                subtitle = "AI Voice",
                                icon = Icons.Filled.SmartToy,
                                accentColor = Color(0xFFB388FF),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("ai_roleplay") }
                            
                            GlassmorphicActionCard(
                                title = "Speaking Word",
                                subtitle = "Pronunciation",
                                icon = Icons.Filled.Mic,
                                accentColor = Color(0xFF00E5FF),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("speaking_practice") }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GlassmorphicActionCard(
                                title = "Speaking",
                                subtitle = "50+ real-life scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("pro_practice") }
                            
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }"""

content = content.replace(old_actions, new_actions)

with open(path, 'w') as f:
    f.write(content)
