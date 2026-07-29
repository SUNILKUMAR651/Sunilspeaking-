with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

if 'composable("mock_interview")' not in content:
    content = content.replace(
'''            composable("ai_debate") {
                PlaceholderScreen("AI Debate Practice")
            }''',
'''            composable("ai_debate") {
                PlaceholderScreen("AI Debate Practice")
            }
            composable("mock_interview") {
                PlaceholderScreen("Mock Interview AI Simulation")
            }''')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
