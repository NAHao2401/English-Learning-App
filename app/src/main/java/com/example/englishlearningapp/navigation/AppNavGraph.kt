package com.example.englishlearningapp.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
import com.example.englishlearningapp.data.local.db.DatabaseProvider
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.features.auth.ui.LoginScreen
import com.example.englishlearningapp.features.auth.ui.RegisterScreen
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModel
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModelFactory
import com.example.englishlearningapp.features.chat.ui.ChatScreen
import com.example.englishlearningapp.features.home.ui.HomeScreen
import com.example.englishlearningapp.features.lesson.ui.LessonDetailScreen
import com.example.englishlearningapp.features.lesson.ui.LessonListScreen
import com.example.englishlearningapp.features.lesson.ui.LessonResultScreen
import com.example.englishlearningapp.features.lesson.ui.TopicListScreen
import com.example.englishlearningapp.features.lesson.viewmodel.LessonViewModel
import com.example.englishlearningapp.features.profile.ui.ProfileScreen
import com.example.englishlearningapp.features.profile.viewmodel.ProfileViewModel
import com.example.englishlearningapp.features.profile.viewmodel.ProfileViewModelFactory
import com.example.englishlearningapp.features.progress.ui.ProgressScreen
import com.example.englishlearningapp.features.progress.viewmodel.ProgressViewModel
import com.example.englishlearningapp.features.scan.ui.ScanResultScreen
import com.example.englishlearningapp.features.scan.ui.ScanScreen
import com.example.englishlearningapp.features.scan.viewmodel.ScanViewModel
import com.example.englishlearningapp.features.scan.viewmodel.ScanViewModelFactory
import com.example.englishlearningapp.features.speaking.ui.SpeakingScreen
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingViewModel
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingViewModelFactory
import com.example.englishlearningapp.features.usertopic.UserTopicViewModel
import com.example.englishlearningapp.features.usertopic.UserTopicViewModelFactory
import com.example.englishlearningapp.features.usertopic.ui.UserTopicDetailScreen
import com.example.englishlearningapp.features.usertopic.ui.UserTopicListScreen
import com.example.englishlearningapp.features.vocab.ui.AllTopicsScreen
import com.example.englishlearningapp.features.vocab.ui.CefrLevelDetailScreen
import com.example.englishlearningapp.features.vocab.ui.FlashcardScreen
import com.example.englishlearningapp.features.vocab.ui.FreePracticeChallengeScreen
import com.example.englishlearningapp.features.vocab.ui.FreePracticeListeningScreen
import com.example.englishlearningapp.features.vocab.ui.FreePracticeQuizScreen
import com.example.englishlearningapp.features.vocab.ui.LearnedWordsScreen
import com.example.englishlearningapp.features.vocab.ui.ReviewQuizChallengeScreen
import com.example.englishlearningapp.features.vocab.ui.ReviewQuizListeningScreen
import com.example.englishlearningapp.features.vocab.ui.ReviewQuizScreen
import com.example.englishlearningapp.features.vocab.ui.SavedVocabScreen
import com.example.englishlearningapp.features.vocab.ui.SelfPracticeChallengeScreen
import com.example.englishlearningapp.features.vocab.ui.SelfPracticeListeningScreen
import com.example.englishlearningapp.features.vocab.ui.SelfPracticeQuizScreen
import com.example.englishlearningapp.features.vocab.ui.StudyFlashcardSessionScreen
import com.example.englishlearningapp.features.vocab.ui.TopicDetailScreen
import com.example.englishlearningapp.features.vocab.ui.VocabScreen
import com.example.englishlearningapp.features.vocab.ui.VocabSearchScreen
import com.example.englishlearningapp.features.vocab.ui.vocabScreenBackground
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModelFactory

@SuppressLint("UnrememberedGetBackStackEntry", "UnusedMaterial3ScaffoldPaddingParameter")
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
    val userTopicViewModel: UserTopicViewModel =
        composeViewModel(factory = UserTopicViewModelFactory(context))
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context.applicationContext)
    )

    val appDataStore = AppDataStore(context.applicationContext)
    val appDatabase = DatabaseProvider.getDatabase(context.applicationContext)
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
    val contentBackgroundColor = if (isVocabRelatedRoute(currentRoute)) {
        vocabScreenBackground()
    } else {
        MaterialTheme.colorScheme.background
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemClick = { route -> navController.navigateBottomItem(route) }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(contentBackgroundColor)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
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
                        onVocabularyClick = { navController.navigateBottomItem(Screen.Vocab.route) },
                        onProgressClick = { navController.navigateBottomItem(Screen.Progress.route) },
                        onAiScanClick = { navController.navigateBottomItem(Screen.AiScan.route) },
                        onSpeakingClick = { navController.navigateBottomItem(Screen.Speaking.route) },
                        onContinueLearningClick = { navController.navigateBottomItem(Screen.TopicList.route) },
                        onNavigateToChat = { navController.navigate(Screen.Chat.route) }
                    )
                }

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

                composable(Screen.LearnedWords.route) {
                    LearnedWordsScreen(navController = navController)
                }

                composable(Screen.ReviewQuiz.route) {
                    ReviewQuizScreen(navController = navController)
                }

                composable(Screen.ReviewQuizListening.route) {
                    ReviewQuizListeningScreen(navController = navController)
                }

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

                composable(Screen.SelfPracticeNormal.route) {
                    SelfPracticeQuizScreen(navController = navController, userTopicViewModel = userTopicViewModel)
                }

                composable(Screen.SelfPracticeListening.route) {
                    SelfPracticeListeningScreen(navController = navController, userTopicViewModel = userTopicViewModel)
                }

                composable(Screen.SelfPracticeChallenge.route) {
                    SelfPracticeChallengeScreen(navController = navController, userTopicViewModel = userTopicViewModel)
                }

                composable(Screen.UserTopics.route) {
                    UserTopicListScreen(navController = navController, userTopicVm = userTopicViewModel)
                }

                composable(Screen.SavedVocab.route) {
                    SavedVocabScreen(navController = navController)
                }

                composable(Screen.TopicDetail.route) { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString("topicId")?.toIntOrNull()
                        ?: return@composable
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
                    val userTopicId = backStackEntry.arguments?.getString("userTopicId")?.toIntOrNull()
                        ?: return@composable
                    UserTopicDetailScreen(
                        navController = navController,
                        userTopicVm = userTopicViewModel,
                        userTopicId = userTopicId
                    )
                }

                composable(Screen.Flashcard.route) { backStackEntry ->
                    val topicId = backStackEntry.arguments?.getString("topicId")?.toIntOrNull()
                        ?: return@composable
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
                            lessonViewModel.selectAnswer(
                                lessonId = lessonId,
                                questionId = questionId,
                                answer = answer
                            )
                        },
                        onSubmitClick = {
                            lessonViewModel.submitLesson(
                                lessonId = lessonId,
                                onSuccess = { navController.navigate(Screen.LessonResult.route) }
                            )
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.LessonResult.route) {
                    LessonResultScreen(
                        result = lessonUiState.submitResult,
                        onRetryClick = { navController.popBackStack() },
                        onContinueClick = {
                            navController.navigate(Screen.TopicList.route) {
                                popUpTo(Screen.TopicList.route) { inclusive = true }
                            }
                        },
                        onProgressClick = { navController.navigate(Screen.Progress.route) }
                    )
                }

                composable(Screen.Progress.route) {
                    LaunchedEffect(Unit) { progressViewModel.loadProgress() }
                    ProgressScreen(
                        summary = progressUiState.summary,
                        isLoading = progressUiState.isLoading,
                        errorMessage = progressUiState.errorMessage,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.AiScan.route) {
                    val factory = ScanViewModelFactory(context)
                    val scanViewModel: ScanViewModel = viewModel(factory = factory)
                    ScanScreen(
                        viewModel = scanViewModel,
                        onNavigateToResult = {
                            navController.navigate(Screen.ScanResult.route) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Scan.route) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.AiScan.route) {
                            popUpTo(Screen.Scan.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                composable(Screen.ScanResult.route) {
                    val scanViewModel: ScanViewModel = viewModel(
                        viewModelStoreOwner = navController.getBackStackEntry(Screen.AiScan.route),
                        factory = ScanViewModelFactory(context)
                    )
                    ScanResultScreen(
                        viewModel = scanViewModel,
                        onBack = {
                            scanViewModel.clearResults()
                            navController.popBackStack()
                        },
                        onScanAgain = {
                            scanViewModel.clearResults()
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.Speaking.route) {
                    val factory = SpeakingViewModelFactory(
                        context = context.applicationContext,
                        speakingPracticeDao = appDatabase.speakingPracticeDao(),
                        speakingApiService = RetrofitClient.speakingApiService,
                        appDataStore = appDataStore
                    )
                    val speakingViewModel: SpeakingViewModel = viewModel(factory = factory)
                    SpeakingScreen(
                        viewModel = speakingViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Chat.route) {
                    ChatScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onLogoutSuccess = {
                            authViewModel.resetState()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}

private data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(label = "Home", route = Screen.Home.route, icon = Icons.Rounded.Home),
    BottomNavItem(label = "Lessons", route = Screen.TopicList.route, icon = Icons.Rounded.AutoStories),
    BottomNavItem(label = "Vocabulary", route = Screen.Vocab.route, icon = Icons.Rounded.Translate),
    BottomNavItem(label = "AI Scan", route = Screen.AiScan.route, icon = Icons.Rounded.CameraAlt),
    BottomNavItem(label = "Speaking", route = Screen.Speaking.route, icon = Icons.Rounded.Mic),
    BottomNavItem(label = "Progress", route = Screen.Progress.route, icon = Icons.Rounded.BarChart),
    BottomNavItem(label = "Profile", route = Screen.Profile.route, icon = Icons.Rounded.Person)
)

private fun shouldShowBottomBar(route: String?): Boolean {
    if (route == null) return false

    val primary = listOf(
        Screen.Home.route,
        Screen.TopicList.route,
        Screen.Vocab.route,
        Screen.Vocabulary.route,
        Screen.AiScan.route,
        Screen.Speaking.route,
        Screen.Progress.route,
        Screen.Profile.route
    )

    if (route in primary) return true
    if (isVocabRelatedRoute(route)) return true

    return route == Screen.LessonList.route
}

private fun isVocabRelatedRoute(route: String?): Boolean {
    if (route == null) return false

    val vocabRoutes = listOf(
        Screen.Vocab.route,
        Screen.Vocabulary.route,
        Screen.AllTopics.route,
        Screen.VocabSearch.route,
        Screen.UserTopics.route,
        Screen.SavedVocab.route,
        Screen.Review.route,
        Screen.LearnedWords.route,
        Screen.ReviewQuiz.route,
        Screen.ReviewQuizListening.route,
        Screen.ReviewQuizChallenge.route,
        Screen.FreePracticeNormal.route,
        Screen.FreePracticeListening.route,
        Screen.FreePracticeChallenge.route,
        Screen.SelfPracticeNormal.route,
        Screen.SelfPracticeListening.route,
        Screen.SelfPracticeChallenge.route
    )

    val vocabRoutePrefixes = listOf(
        "user_topic_detail/",
        "topic_detail/",
        "cefr_detail/",
        "cefr_level_detail/",
        "flashcard/",
        "study_flashcard/"
    )

    return route in vocabRoutes || vocabRoutePrefixes.any { route.startsWith(it) }
}

private fun isBottomItemSelected(currentRoute: String?, itemRoute: String): Boolean {
    return when (itemRoute) {
        Screen.TopicList.route -> {
            currentRoute == Screen.TopicList.route ||
                currentRoute == Screen.LessonList.route ||
                currentRoute == Screen.LessonDetail.route
        }
        Screen.Vocab.route -> isVocabRelatedRoute(currentRoute)
        else -> currentRoute == itemRoute
    }
}

private fun NavController.navigateBottomItem(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun AppBottomNavigationBar(currentRoute: String?, onItemClick: (String) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        NavigationBar(
            modifier = Modifier.height(72.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = isBottomItemSelected(currentRoute = currentRoute, itemRoute = item.route)
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val hovered by interactionSource.collectIsHoveredAsState()
                val showIconBackground = pressed || hovered
                val iconColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onItemClick(item.route) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = if (showIconBackground) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComingSoonScreen(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6FF))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1D1B2F)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF77738A),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
