package com.example.englishlearningapp.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.englishlearningapp.core.session.SessionManager
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.features.auth.ui.LoginScreen
import com.example.englishlearningapp.features.auth.ui.RegisterScreen
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModel
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModelFactory
import com.example.englishlearningapp.features.learn.ui.LearnScreen
import com.example.englishlearningapp.features.home.ui.HomeScreen
import com.example.englishlearningapp.features.lesson.ui.LessonDetailScreen
import com.example.englishlearningapp.features.lesson.ui.LessonListScreen
import com.example.englishlearningapp.features.lesson.ui.LessonResultScreen
import com.example.englishlearningapp.features.lesson.ui.TopicListScreen
import com.example.englishlearningapp.features.lesson.viewmodel.LessonViewModel
import com.example.englishlearningapp.features.profile.ui.ProfileScreen
import com.example.englishlearningapp.features.progress.ui.ProgressScreen
import com.example.englishlearningapp.features.progress.viewmodel.ProgressViewModel
import com.example.englishlearningapp.features.scan.ui.ScanScreen
import com.example.englishlearningapp.features.usertopic.ui.UserTopicDetailScreen
import com.example.englishlearningapp.features.usertopic.ui.UserTopicListScreen
import com.example.englishlearningapp.features.vocab.ui.CefrLevelDetailScreen
import com.example.englishlearningapp.features.vocab.ui.FlashcardScreen
import com.example.englishlearningapp.features.vocab.ui.LearnedWordsScreen
import com.example.englishlearningapp.features.vocab.ui.ReviewQuizChallengeScreen
import com.example.englishlearningapp.features.vocab.ui.ReviewQuizListeningScreen
import com.example.englishlearningapp.features.vocab.ui.ReviewQuizScreen
import com.example.englishlearningapp.features.vocab.ui.FreePracticeChallengeScreen
import com.example.englishlearningapp.features.vocab.ui.FreePracticeListeningScreen
import com.example.englishlearningapp.features.vocab.ui.FreePracticeQuizScreen
import com.example.englishlearningapp.features.vocab.ui.AllTopicsScreen
import com.example.englishlearningapp.features.vocab.ui.SavedVocabScreen
import com.example.englishlearningapp.features.vocab.ui.VocabSearchScreen
import com.example.englishlearningapp.features.vocab.ui.StudyFlashcardSessionScreen
import com.example.englishlearningapp.features.vocab.ui.TopicDetailScreen
import com.example.englishlearningapp.features.vocab.ui.VocabScreen
import com.example.englishlearningapp.features.vocab.ui.SelfPracticeQuizScreen
import com.example.englishlearningapp.features.vocab.ui.SelfPracticeListeningScreen
import com.example.englishlearningapp.features.vocab.ui.SelfPracticeChallengeScreen
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModelFactory

@Composable
fun AppNavGraph(
    context: Context,
    startDestination: String
) {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext)
    )
    val lessonViewModel: LessonViewModel = viewModel()
    val progressViewModel: ProgressViewModel = viewModel()
    val vocabViewModel: VocabViewModel = composeViewModel(factory = VocabViewModelFactory(context))

    val userTopicViewModel: com.example.englishlearningapp.features.usertopic.UserTopicViewModel =
        composeViewModel(factory = com.example.englishlearningapp.features.usertopic.UserTopicViewModelFactory(context))

    val appDataStore = AppDataStore(context.applicationContext)
    val userName by appDataStore.userName.collectAsState(initial = "Learner")

    val lessonUiState by lessonViewModel.uiState.collectAsState()
    val progressUiState by progressViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        SessionManager.unauthorizedEvent.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = shouldShowBottomBar(currentRoute)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigateBottomItem(route)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = { navController.popBackStack() }
                )
            }

            composable(Screen.Home.route) {
                LaunchedEffect(Unit) { progressViewModel.loadProgress() }
                HomeScreen(
                    userName = userName.ifBlank { "Learner" },
                    totalXp = progressUiState.summary?.total_xp ?: 0,
                    streakCount = progressUiState.summary?.streak_count ?: 0,
                    completedLessons = progressUiState.summary?.completed_lessons ?: 0,
                    totalLessons = progressUiState.summary?.total_lessons ?: 0,
                    completionPercent = progressUiState.summary?.completion_percent ?: 0,
                    currentLevel = progressUiState.summary?.current_level ?: "Beginner",
                    isProgressLoading = progressUiState.isLoading,
                    progressErrorMessage = progressUiState.errorMessage,
                    onLessonsClick = { navController.navigateBottomItem(Screen.TopicList.route) },
                    onVocabularyClick = { navController.navigateBottomItem(Screen.Vocabulary.route) },
                    onProgressClick = { navController.navigateBottomItem(Screen.Progress.route) },
                    onAiScanClick = { navController.navigateBottomItem(Screen.AiScan.route) },
                    onSpeakingClick = { navController.navigateBottomItem(Screen.Speaking.route) },
                    onContinueLearningClick = { navController.navigateBottomItem(Screen.TopicList.route) },
                    onLogoutClick = {
                        authViewModel.logout {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            // Core app routes from previous merge: keep both 'Vocabulary' (bottom nav) and 'Vocab' (full feature)
            composable(Screen.Vocab.route) {
                VocabScreen(navController = navController, vocabVm = vocabViewModel)
            }

            composable(Screen.AllTopics.route) {
                AllTopicsScreen(navController = navController, viewModel = vocabViewModel)
            }

            composable(Screen.VocabSearch.route) {
                VocabSearchScreen(navController = navController, vocabVm = vocabViewModel)
            }

            composable(Screen.Vocabulary.route) {
                ComingSoonScreen(
                    title = "Vocabulary",
                    subtitle = "Practice and review your saved words"
                )
            }

            composable(Screen.LearnedWords.route) { LearnedWordsScreen(navController = navController) }

            composable(Screen.ReviewQuiz.route) { ReviewQuizScreen(navController = navController) }

            composable(Screen.ReviewQuizListening.route) { ReviewQuizListeningScreen(navController = navController) }

            composable(Screen.ReviewQuizChallenge.route) {
                ReviewQuizChallengeScreen(
                    navController = navController,
                    viewModel = composeViewModel(factory = VocabViewModelFactory(context))
                )
            }

            composable(Screen.FreePracticeNormal.route) {
                FreePracticeQuizScreen(navController = navController, vocabViewModel = vocabViewModel)
            }

            composable(Screen.FreePracticeListening.route) {
                FreePracticeListeningScreen(navController = navController, vocabViewModel = vocabViewModel)
            }

            composable(Screen.FreePracticeChallenge.route) {
                FreePracticeChallengeScreen(navController = navController, vocabViewModel = vocabViewModel)
            }

            // Self practice flows (do NOT call rateQuizAnswer or rateVocabulary here)
            composable(Screen.SelfPracticeNormal.route) {
                SelfPracticeQuizScreen(navController = navController, userTopicViewModel = userTopicViewModel)
            }

            composable(Screen.SelfPracticeListening.route) {
                SelfPracticeListeningScreen(navController = navController, userTopicViewModel = userTopicViewModel)
            }

            composable(Screen.SelfPracticeChallenge.route) {
                SelfPracticeChallengeScreen(navController = navController, userTopicViewModel = userTopicViewModel)
            }

            composable(Screen.UserTopics.route) { UserTopicListScreen(navController = navController, userTopicVm = userTopicViewModel) }

            composable(Screen.SavedVocab.route) { SavedVocabScreen(navController = navController) }

            composable(Screen.TopicDetail.route) { backStackEntry ->
                val topicIdArg = backStackEntry.arguments?.getString("topicId")
                val topicId = topicIdArg?.toIntOrNull() ?: return@composable
                TopicDetailScreen(navController = navController, topicId = topicId)
            }



            composable(
                route = Screen.CefrLevelDetail.route,
                arguments = listOf(navArgument("level") { type = NavType.StringType })
            ) { backStackEntry ->
                val level = backStackEntry.arguments?.getString("level") ?: return@composable
                CefrLevelDetailScreen(navController = navController, level = level)
            }

            composable(Screen.UserTopicDetail.route) { backStackEntry ->
                val userTopicIdArg = backStackEntry.arguments?.getString("userTopicId")
                val userTopicId = userTopicIdArg?.toIntOrNull() ?: return@composable
                UserTopicDetailScreen(navController = navController, userTopicId = userTopicId)
            }

            composable(Screen.Flashcard.route) { backStackEntry ->
                val topicIdArg = backStackEntry.arguments?.getString("topicId")
                val topicId = topicIdArg?.toIntOrNull() ?: return@composable
                FlashcardScreen(navController = navController, topicId = topicId)
            }

            composable(
                route = Screen.StudyFlashcard.route,
                arguments = listOf(navArgument("topicId") { type = NavType.IntType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getInt("topicId") ?: return@composable
                StudyFlashcardSessionScreen(navController = navController, topicId = topicId)
            }

            composable(Screen.TopicList.route) {
                LaunchedEffect(Unit) { lessonViewModel.loadTopics() }
                TopicListScreen(
                    topics = lessonUiState.topics,
                    isLoading = lessonUiState.isLoading,
                    errorMessage = lessonUiState.errorMessage,
                    onTopicClick = { topicId -> navController.navigate(Screen.LessonList.createRoute(topicId)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.LessonList.route,
                arguments = listOf(navArgument("topicId") { type = NavType.IntType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getInt("topicId") ?: return@composable
                LaunchedEffect(topicId) { lessonViewModel.loadLessonsByTopic(topicId) }
                LessonListScreen(
                    lessons = lessonUiState.lessons,
                    isLoading = lessonUiState.isLoading,
                    errorMessage = lessonUiState.errorMessage,
                    onLessonClick = { lessonId -> navController.navigate(Screen.LessonDetail.createRoute(lessonId)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.LessonDetail.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
                LaunchedEffect(lessonId) { lessonViewModel.loadQuestions(lessonId) }
                LessonDetailScreen(
                    questions = lessonUiState.questions,
                    selectedAnswers = lessonUiState.selectedAnswers,
                    isLoading = lessonUiState.isLoading,
                    isSavingAnswer = lessonUiState.isSavingAnswer,
                    errorMessage = lessonUiState.errorMessage,
                    onSelectAnswer = { questionId, answer ->
                        lessonViewModel.selectAnswer(lessonId = lessonId, questionId = questionId, answer = answer)
                    },
                    onSubmitClick = {
                        lessonViewModel.submitLesson(lessonId = lessonId, onSuccess = { navController.navigate(Screen.LessonResult.route) })
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.LessonResult.route) {
                LessonResultScreen(
                    result = lessonUiState.submitResult,
                    onRetryClick = { navController.popBackStack() },
                    onContinueClick = {
                        navController.navigate(Screen.TopicList.route) { popUpTo(Screen.TopicList.route) { inclusive = true } }
                    },
                    onProgressClick = { navController.navigate(Screen.Progress.route) }
                )
            }

            composable(Screen.Progress.route) {
                LaunchedEffect(Unit) { progressViewModel.loadProgress() }
                ProgressScreen(summary = progressUiState.summary, isLoading = progressUiState.isLoading, errorMessage = progressUiState.errorMessage, onBackClick = { navController.popBackStack() })
            }

            composable(Screen.AiScan.route) {
                ComingSoonScreen(title = "AI Scan", subtitle = "Scan text and learn new English words")
            }

            composable(Screen.Speaking.route) {
                ComingSoonScreen(title = "Speaking", subtitle = "Practice pronunciation and speaking skills")
            }

            composable(Screen.Profile.route) { ComingSoonScreen(title = "Profile", subtitle = "Manage your account and learning settings") }
        }
    }
}


private data class BottomNavItem(
    val label: String,
    val shortLabel: String,
    val route: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(label = "Home", shortLabel = "Home", route = Screen.Home.route, icon = Icons.Rounded.Home),
    BottomNavItem(label = "Lessons", shortLabel = "Lessons", route = Screen.TopicList.route, icon = Icons.Rounded.AutoStories),
    BottomNavItem(label = "Vocabulary", shortLabel = "Vocab", route = Screen.Vocab.route, icon = Icons.Rounded.Translate),
    BottomNavItem(label = "AI Scan", shortLabel = "Scan", route = Screen.AiScan.route, icon = Icons.Rounded.CameraAlt),
    BottomNavItem(label = "Speaking", shortLabel = "Speak", route = Screen.Speaking.route, icon = Icons.Rounded.Mic),
    BottomNavItem(label = "Progress", shortLabel = "Progress", route = Screen.Progress.route, icon = Icons.Rounded.BarChart),
    BottomNavItem(label = "Profile", shortLabel = "Profile", route = Screen.Profile.route, icon = Icons.Rounded.Person)
)

private fun shouldShowBottomBar(route: String?): Boolean {
    if (route == null) return false

    // Always show for primary bottom-nav routes
    val primary = listOf(
        Screen.Home.route,
        Screen.TopicList.route,
        Screen.Vocabulary.route,
        Screen.AiScan.route,
        Screen.Speaking.route,
        Screen.Progress.route,
        Screen.Profile.route
    )
    if (route in primary) return true

    // Also show bottom bar on any Vocab-related screens (including parameterized routes)
    val vocabPrefixes = listOf(
        Screen.Vocab.route,                // "vocab"
        Screen.VocabSearch.route,
        Screen.SavedVocab.route,           // "saved_vocab"
        Screen.ReviewQuiz.route,           // "review_quiz"
        "review_quiz_listening",
        "review_quiz_challenge",
        Screen.LearnedWords.route,         // "learned_words"
        "topic_detail/",
        "cefr_detail/",
        "cefr_level_detail/",
        "flashcard/",
        "study_flashcard/"
    )

    if (vocabPrefixes.any { route.startsWith(it) }) return true

    // keep lessons behavior
    if (route == Screen.LessonList.route) return true

    return false
}

private fun isBottomItemSelected(currentRoute: String?, itemRoute: String): Boolean {
    return when (itemRoute) {
        Screen.TopicList.route -> (currentRoute == Screen.TopicList.route || currentRoute == Screen.LessonList.route || currentRoute == Screen.LessonDetail.route)
        else -> currentRoute == itemRoute
    }
}

private fun NavController.navigateBottomItem(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun AppBottomNavigationBar(currentRoute: String?, onItemClick: (String) -> Unit) {
    Surface(color = Color.White, tonalElevation = 8.dp, shadowElevation = 10.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        NavigationBar(modifier = Modifier.height(72.dp), containerColor = Color.White, tonalElevation = 0.dp) {
            bottomNavItems.forEach { item ->
                val selected = isBottomItemSelected(currentRoute = currentRoute, itemRoute = item.route)
                NavigationBarItem(
                    selected = selected,
                    onClick = { onItemClick(item.route) },
                    icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                    label = { Text(text = item.shortLabel, fontSize = 9.sp, lineHeight = 10.sp, maxLines = 1, overflow = TextOverflow.Clip) },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF6C63FF), selectedTextColor = Color(0xFF6C63FF), indicatorColor = Color(0xFFE9E7FF), unselectedIconColor = Color(0xFF8D8A99), unselectedTextColor = Color(0xFF8D8A99))
                )
            }
        }
    }
}

@Composable
private fun ComingSoonScreen(title: String, subtitle: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F6FF)).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall, color = Color(0xFF1D1B2F))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF77738A), modifier = Modifier.padding(top = 8.dp))
        }
    }
}
