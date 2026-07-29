path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, 'r') as f:
    content = f.read()

import re

old_cards = """                        Row(
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
                                title = "Speaking Practice",
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
                        }"""

new_cards = """                        Row(
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
                                title = "Practice",
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
                                title = "Speaking Practice",
                                subtitle = "50+ scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("pro_practice") }
                            
                            GlassmorphicActionCard(
                                title = "Speaking Fast",
                                subtitle = "Job Interviews",
                                icon = Icons.Filled.TrendingUp,
                                accentColor = Color(0xFFFF3333),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("speaking_fast") }
                        }"""

content = content.replace(old_cards, new_cards)

old_games = """                        item {
                            GlassmorphicActionCard(
                                title = "Speaking",
                                subtitle = "50+ scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.width(160.dp)
                            ) { onNavigate("pro_practice") }
                        }"""

new_games = """                        item {
                            GlassmorphicActionCard(
                                title = "Speaking Practice",
                                subtitle = "50+ scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.width(160.dp)
                            ) { onNavigate("pro_practice") }
                        }"""

content = content.replace(old_games, new_games)

with open(path, 'w') as f:
    f.write(content)
