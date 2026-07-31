package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.screens.ProPracticeScreen
import com.example.ui.screens.PracticeRunScreen

import com.example.ui.theme.LexiCoreTheme
import com.example.viewmodel.LexiViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import android.widget.Toast

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    var onPaymentSuccessCallback: (() -> Unit)? = null
    var onPaymentErrorCallback: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Checkout.preload(applicationContext)
        enableEdgeToEdge()
        setContent {
            val viewModel: LexiViewModel = viewModel()
            val userProfile by viewModel.userProfile.collectAsState()
            val darkTheme = when (userProfile.themePreference) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            LexiCoreTheme(darkTheme = darkTheme) {
                val authStatus by viewModel.authStatus.collectAsState()
                
                if (authStatus is com.example.viewmodel.AuthStatus.Authenticated) {
                    val userProfile by viewModel.userProfile.collectAsState()
                    if (userProfile.isOnboardingCompleted) {
                        LexiApp(viewModel = viewModel)
                    } else {
                        OnboardingScreen(
                            viewModel = viewModel,
                            onFinish = { }
                        )
                    }
                } else {
                    AuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = { }
                    )
                }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?, paymentData: PaymentData?) {
        onPaymentSuccessCallback?.invoke()
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        onPaymentErrorCallback?.invoke(response ?: "Payment failed")
    }
    
    fun startPayment(amount: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        onPaymentSuccessCallback = onSuccess
        onPaymentErrorCallback = onError
        val apiKey = "rzp_test_TJFnozzpPbub2B"
        if (apiKey.isEmpty() || apiKey == "MY_RAZORPAY_KEY" || apiKey == "rzp_test_mock") {
            // Mock processing handler
            Toast.makeText(this, "Processing Mock Payment...", Toast.LENGTH_SHORT).show()
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                onPaymentSuccess("mock_payment_id", null)
            }, 1500)
            return
        }
        val checkout = Checkout()
        checkout.setKeyID(apiKey)
        try {
            val options = JSONObject()
            options.put("name", "Xevrino Pro")
            options.put("description", "Premium Subscription")
            options.put("currency", "INR")
            options.put("amount", amount * 100) // in paise
            options.put("theme.color", "#6B4EE6")
            
            val prefill = JSONObject()
            prefill.put("email", "test@example.com")
            prefill.put("contact", "9999999999")
            options.put("prefill", prefill)
            
            checkout.open(this, options)
        } catch (e: Exception) {
            onError(e.message ?: "Error launching payment")
        }
    }
}

@Composable
fun LexiApp(viewModel: LexiViewModel) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("home") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && userProfile.notificationsEnabled) {
            com.example.utils.NotificationHelper.scheduleDailyReminder(context, userProfile.reminderHour, userProfile.reminderMinute)
        }
    }
    
    LaunchedEffect(userProfile.notificationsEnabled, userProfile.reminderHour, userProfile.reminderMinute) {
        if (!userProfile.notificationsEnabled) {
            com.example.utils.NotificationHelper.cancelReminder(context)
            return@LaunchedEffect
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                com.example.utils.NotificationHelper.scheduleDailyReminder(context, userProfile.reminderHour, userProfile.reminderMinute)
            }
        } else {
            com.example.utils.NotificationHelper.scheduleDailyReminder(context, userProfile.reminderHour, userProfile.reminderMinute)
        }
    }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarMenu(
                viewModel = viewModel,
                onNavigate = { route ->
                    currentRoute = route
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onCloseDrawer = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = {
                        currentRoute = "home"
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home", maxLines = 1) },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    selected = currentRoute == "learn",
                    onClick = {
                        currentRoute = "learn"
                        navController.navigate("learn") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = "Learn") },
                    label = { Text("Learn", maxLines = 1) },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    selected = currentRoute == "practice",
                    onClick = {
                        currentRoute = "practice"
                        navController.navigate("practice") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Practice") },
                    label = { Text("Practice", maxLines = 1) },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    selected = currentRoute == "ai_teacher",
                    onClick = {
                        currentRoute = "ai_teacher"
                        navController.navigate("ai_teacher") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.SmartToy, contentDescription = "AI English Teacher") },
                    label = { Text("AI", maxLines = 1) },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = {
                        currentRoute = "profile"
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile", maxLines = 1) },
                    alwaysShowLabel = false
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onNavigate = { route ->
                        if (route.startsWith("learn_category/")) {
                            val category = route.removePrefix("learn_category/")
                            viewModel.selectLetter(null)
                            viewModel.selectCategory(category)
                            viewModel.updateSearchQuery("")
                            navController.navigate("list/$category Words")
                        } else if (route.startsWith("list/")) {
                            val category = route.removePrefix("list/")
                            viewModel.selectLetter(null)
                            viewModel.selectCategory(category)
                            viewModel.updateSearchQuery("")
                            navController.navigate(route)
                        } else {
                            if (route == "speaking_practice") {
                                viewModel.selectCategory("Speaking Practice")
                            }
                            navController.navigate(route)
                        }
                    }
                )
            }
            
            composable("learning_path") {
                com.example.ui.screens.LearningPathScreen(
                    viewModel = viewModel,
                    onNavigateToLesson = { lessonId ->
                        navController.navigate("lesson_overview/$lessonId")
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                "lesson_overview/{lessonId}",
                arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
                com.example.ui.screens.LessonOverviewScreen(
                    lessonId = lessonId,
                    viewModel = viewModel,
                    onStartLearning = {
                        navController.navigate("active_lesson/$lessonId")
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                "active_lesson/{lessonId}",
                arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
                com.example.ui.screens.ActiveLessonScreen(
                    lessonId = lessonId,
                    viewModel = viewModel,
                    onFinish = {
                        // Increase XP
                        viewModel.addXp(15)
                        navController.popBackStack("learning_path", inclusive = false)
                    },
                    onClose = { navController.popBackStack() }
                )
            }
            
            composable(
                "level/{title}",
                arguments = listOf(navArgument("title") { type = NavType.StringType })
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: "Beginner level"
                com.example.ui.screens.LevelScreen(
                    title = title,
                    onNavigate = { route ->
                        if (route.startsWith("interactive_practice/")) {
                            val fullPath = route.removePrefix("interactive_practice/")
                            val category = fullPath.substringBefore(" Part ")
                            viewModel.selectLetter(null)
                            viewModel.selectCategory(category)
                            viewModel.updateSearchQuery("")
                            navController.navigate(route)
                        } else if (route.startsWith("list/")) {
                            val category = route.removePrefix("list/")
                            viewModel.selectLetter(null)
                            viewModel.selectCategory(category)
                            viewModel.updateSearchQuery("")
                            navController.navigate(route)
                        } else {
                            navController.navigate(route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                "interactive_practice/{title}",
                arguments = listOf(navArgument("title") { type = NavType.StringType })
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: "Practice"
                com.example.ui.screens.InteractivePracticeScreen(
                    title = title,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                "list/{title}",
                arguments = listOf(navArgument("title") { type = NavType.StringType })
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: "Words"
                WordListScreen(
                    title = title,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onWordClick = { word ->
                        viewModel.clearAiSentence()
                        navController.navigate("detail/$word")
                    }
                )
            }

            composable(
                "detail/{word}",
                arguments = listOf(navArgument("word") { type = NavType.StringType })
            ) { backStackEntry ->
                val word = backStackEntry.arguments?.getString("word") ?: ""
                WordDetailScreen(
                    wordString = word,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("learn") {
                LearnScreen(viewModel = viewModel, onNavigate = { route ->
                    if (route.startsWith("list/")) {
                        viewModel.selectCategory(null)
                        viewModel.selectLetter(null)
                        viewModel.updateSearchQuery("")
                    }
                    navController.navigate(route)
                })
            }
            composable("practice") {
                PracticeScreen(
                    viewModel = viewModel,
                    onNavigate = { route ->
                        if (route == "speaking_practice") {
                            viewModel.selectCategory("Speaking Practice")
                            viewModel.selectLetter(null)
                            viewModel.updateSearchQuery("")
                        }
                        navController.navigate(route)
                    }
                )
            }
            
            composable("ai_roleplay") {
                AIRoleplayScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("smart_flashcards") {
                com.example.ui.screens.SmartFlashcardsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("word_battle") {
                com.example.ui.screens.WordBattleScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("speaking_practice") {
                com.example.ui.speaking.SpeakingEvaluationScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("vocabulary_quiz") {
                VocabularyQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("vocabulary_arrangement") {
                com.example.ui.screens.VocabularyArrangementScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("vocabulary_index") {
                com.example.ui.screens.VocabularyIndexScreen(
                    onLetterClick = { letter ->
                        viewModel.selectCategory(null)
                        viewModel.selectLetter(letter.toString())
                        viewModel.updateSearchQuery("")
                        navController.navigate("list/Letter $letter")
                    },
                    onAllWordsClick = {
                        viewModel.selectCategory(null)
                        viewModel.selectLetter(null)
                        viewModel.updateSearchQuery("")
                        navController.navigate("list/All Words")
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("grammar_challenge") {
                GrammarChallengeScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("grammar_learn") {
                PlaceholderScreen("Grammar Lessons")
            }
            composable("reading_learn") {
                PlaceholderScreen("Reading Practice")
            }
            composable("listening_learn") {
                PlaceholderScreen("Listening Practice")
            }
            composable("writing_learn") {
                PlaceholderScreen("Writing Practice")
            }
            composable("conversation_learn") {
                PlaceholderScreen("Conversation Practice")
            }
            composable("ai_debate") {
                PlaceholderScreen("AI Debate Practice")
            }
            composable("mock_interview") {
                PlaceholderScreen("Mock Interview AI Simulation")
            }
            composable("accent_lab") {
                PlaceholderScreen("Accent Lab")
            }
            composable("tone_learn") {
                PlaceholderScreen("Tone & Politeness")
            }
            composable("premium") {
                com.example.ui.screens.PremiumSubscriptionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("scenarios_learn") {
                PlaceholderScreen("Scenarios")
            }
            
            composable("ai_teacher") {
                AITeacherScreen(
                    viewModel = viewModel,
                    onNavigateToCall = { navController.navigate("ai_call") }
                )
            }
            
            composable("ai_call") {
                com.example.ui.screens.AICallScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel, 
                    onNavigateToPremium = { navController.navigate("premium") },
                    onNavigateToCertifications = { navController.navigate("certifications") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            
            composable("settings") {
                com.example.ui.screens.SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToPremium = { navController.navigate("premium") }
                )
            }
            
            composable("certifications") {
                com.example.ui.screens.FluencyCertificationScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("leaderboard") {
                LeaderboardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

                        composable("pro_practice") {
                ProPracticeScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToRun = { scenarioId ->
                        navController.navigate("pro_practice_run/$scenarioId")
                    }
                )
            }
            composable("pro_practice_run/{scenarioId}") { backStackEntry ->
                val scenarioId = backStackEntry.arguments?.getString("scenarioId")?.toIntOrNull() ?: 0
                PracticeRunScreen(
                    lessonId = scenarioId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("speaking_fast") {
                com.example.ui.screens.SpeakingPracticeDashboard(
                    onBack = { navController.popBackStack() },
                    onNavigateToCategory = { categoryId ->
                        navController.navigate("speaking_fast_category/$categoryId")
                    }
                )
            }
            composable("speaking_fast_category/{categoryId}") { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "job_interview"
                com.example.ui.screens.SpeakingTopicDetailScreen(
                    categoryId = categoryId,
                    onBack = { navController.popBackStack() },
                    onNavigateToTask = { taskId ->
                        navController.navigate("speaking_fast_run/$taskId")
                    }
                )
            }
            composable("speaking_fast_run/{taskId}") { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull() ?: 1
                com.example.ui.screens.SpeakingTaskRunScreen(
                    taskId = taskId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("bubble_pop") {
                BubblePopScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("crossword_connect") {
                CrosswordConnectScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("swipe_battle") {
                SwipeBattleScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("word_wheel") {
                WordWheelScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("audio_dictation") {
                AudioDictationScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("weak_words") {
                WeakWordsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("pronunciation_history") {
                PronunciationHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("review") {
                SmartReviewScreen(
                    onBack = { 
                        currentRoute = "home"
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId)
                        } 
                    }
                )
            }
        }
    }
    }
}
