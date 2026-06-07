package com.example.englishlearningapp.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.example.englishlearningapp.features.progress.ui.ProgressScreen
import com.example.englishlearningapp.features.progress.viewmodel.ProgressViewModel
import com.example.englishlearningapp.features.scan.ui.ScanResultScreen
import com.example.englishlearningapp.features.scan.ui.ScanScreen
import com.example.englishlearningapp.features.scan.viewmodel.ScanViewModel
import com.example.englishlearningapp.features.scan.viewmodel.ScanViewModelFactory
import com.example.englishlearningapp.features.speaking.ui.SpeakingScreen
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingViewModel
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingViewModelFactory

@SuppressLint("UnrememberedGetBackStackEntry")
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

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                UserTopicDetailScreen(
                    navController = navController,
                    userTopicVm = userTopicViewModel,
                    userTopicId = userTopicId
                )
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
                    val factory = ScanViewModelFactory(context)
                    val vm: ScanViewModel = viewModel(factory = factory)

                    ScanScreen(
                        viewModel = vm,
                        onNavigateToResult = {
                            navController.navigate(Screen.ScanResult.route)
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

            composable(Screen.Scan.route) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.AiScan.route) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

            composable(Screen.ScanResult.route) {
                val vm: ScanViewModel = viewModel(
                    viewModelStoreOwner = navController.getBackStackEntry(Screen.AiScan.route),
                    factory = ScanViewModelFactory(context)
                )
                ScanResultScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onScanAgain = { navController.popBackStack() }
                )
            }

                composable(Screen.Speaking.route) {
                    val appDataStore = AppDataStore(context)
                    val factory = SpeakingViewModelFactory(
                        context            = context,
                        speakingDao        = appDatabase.speakingPracticeDao(),
                        speakingApiService = RetrofitClient.speakingApiService,
                        appDataStore       = appDataStore
                    )
                    val viewModel: SpeakingViewModel = viewModel(factory = factory)
                    SpeakingScreen(
                        viewModel      = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

            composable(Screen.Chat.route) {
                ChatScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogoutSuccess = {
                        authViewModel.resetState()

        composable(Screen.Home.route) {
            HomeScreen(
                userName = userName.ifBlank { "Learner" },
                onLessonsClick = {
                    navController.navigate(Screen.TopicList.route)
                },
                onVocabularyClick = {
                    navController.navigate(Screen.TopicList.route)
                },
                onProgressClick = {
                    navController.navigate(Screen.Progress.route)
                },
                onAiScanClick = {
                    navController.navigate(Screen.Scan.route)
                },
                onSpeakingClick = {
                    navController.navigate(Screen.Speaking.route)
                },
                onContinueLearningClick = {
                    navController.navigate(Screen.TopicList.route)
                },
                onLogoutClick = {
                    authViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) }
            )
        }

        composable(Screen.TopicList.route) {
            LaunchedEffect(Unit) {
                lessonViewModel.loadTopics()
            }

            TopicListScreen(
                topics = lessonUiState.topics,
                isLoading = lessonUiState.isLoading,
                errorMessage = lessonUiState.errorMessage,
                onTopicClick = { topicId ->
                    navController.navigate(Screen.LessonList.createRoute(topicId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.LessonList.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: return@composable

            LaunchedEffect(topicId) {
                lessonViewModel.loadLessonsByTopic(topicId)
            }

            LessonListScreen(
                lessons = lessonUiState.lessons,
                isLoading = lessonUiState.isLoading,
                errorMessage = lessonUiState.errorMessage,
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.LessonDetail.createRoute(lessonId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.LessonDetail.route,
            arguments = listOf(
                navArgument("lessonId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable

            LaunchedEffect(lessonId) {
                lessonViewModel.loadQuestions(lessonId)
            }

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
                        onSuccess = {
                            navController.navigate(Screen.LessonResult.route)
                        }
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.LessonResult.route) {
            LessonResultScreen(
                result = lessonUiState.submitResult,
                onRetryClick = {
                    navController.popBackStack()
                },
                onContinueClick = {
                    navController.navigate(Screen.TopicList.route) {
                        popUpTo(Screen.TopicList.route) {
                            inclusive = true
                        }
                    }
                },
                onProgressClick = {
                    navController.navigate(Screen.Progress.route)
                }
            )
        }

        composable(Screen.Progress.route) {
            LaunchedEffect(Unit) {
                progressViewModel.loadProgress()
            }

            ProgressScreen(
                summary = progressUiState.summary,
                isLoading = progressUiState.isLoading,
                errorMessage = progressUiState.errorMessage,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Speaking.route) {
            val factory = SpeakingViewModelFactory(
                context = context.applicationContext,
                speakingPracticeDao = appDatabase.speakingPracticeDao(),
                apiService = RetrofitClient.speakingApiService
            )
            val speakingViewModel: SpeakingViewModel = viewModel(factory = factory)

            SpeakingScreen(
                viewModel = speakingViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Chat.route) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Scan.route) {
            val context = LocalContext.current
            val vm: ScanViewModel = viewModel(factory = ScanViewModelFactory(context))
            ScanScreen(
                viewModel = vm,
                onNavigateToResult = { navController.navigate(Screen.ScanResult.route) }
            )
        }

        composable(Screen.ScanResult.route) {
            // Dùng chung ViewModel với ScanScreen (back-stack cùng cấp)
            val vm: ScanViewModel = viewModel(
                viewModelStoreOwner = navController.getBackStackEntry(Screen.Scan.route),
                factory = ScanViewModelFactory(context)
            )
            ScanResultScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onScanAgain = { navController.popBackStack() }
            )
        }
    }
}
