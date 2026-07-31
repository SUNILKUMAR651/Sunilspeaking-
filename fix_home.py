import re
path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, "r") as f:
    content = f.read()

bad_block = """            if (showGoalAnimation) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.6f)).clickable { viewModel.onGoalAnimationShown() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(300.dp)
                        )
                        Text("Daily Goal Achieved! +XP", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            }
        }
    }
}

@Composable"""

good_block = """            }
        }
    }
}

@Composable"""

# Replace all occurrences
content = content.replace(bad_block, good_block)

# Put it back exactly ONCE at the end of HomeScreen which is before HeroBanner
target = """            }
        }
    }
}

@Composable
fun HeroBanner"""

replacement = """            }
            if (showGoalAnimation) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.6f)).clickable { viewModel.onGoalAnimationShown() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(300.dp)
                        )
                        Text("Daily Goal Achieved! +XP", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HeroBanner"""

content = content.replace(target, replacement)

with open(path, "w") as f:
    f.write(content)
