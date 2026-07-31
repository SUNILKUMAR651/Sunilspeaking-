import re
path = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(path, "r") as f:
    content = f.read()

# Remove lines 477 and 608 which are extra '}'
lines = content.split('\n')

# 608 is index 607, 477 is index 476. Wait, line numbers change when we delete!
# Let's just find and replace the exact string that is wrong.

# For HeroBanner:
# 474:             }
# 475:         }
# 476:     }
# 477: }
# 478: 
# 479: @Composable

bad_hero = """            }
            
            }
        }
    }
}

@Composable
fun GlassmorphicActionCard"""

good_hero = """            }
        }
    }
}

@Composable
fun GlassmorphicActionCard"""
content = content.replace(bad_hero, good_hero)

bad_game = """            }
            
            }
        }
    }
}

@Composable
fun GlassmorphicCategoryCard"""

good_game = """            }
        }
    }
}

@Composable
fun GlassmorphicCategoryCard"""
content = content.replace(bad_game, good_game)

with open(path, "w") as f:
    f.write(content)
