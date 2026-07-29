path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, 'r') as f:
    content = f.read()

import re
old_games = """                // Games Section
                item {
                    SectionHeader(title = "Practice Games", onSeeAll = { onNavigate("practice") })
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        item {"""

new_games = """                // Games Section
                item {
                    SectionHeader(title = "Practice Games", onSeeAll = { onNavigate("practice") })
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        item {
                            GlassmorphicActionCard(
                                title = "Speaking Practice",
                                subtitle = "50+ scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.width(160.dp)
                            ) { onNavigate("pro_practice") }
                        }
                        item {"""

content = content.replace(old_games, new_games)

with open(path, 'w') as f:
    f.write(content)
