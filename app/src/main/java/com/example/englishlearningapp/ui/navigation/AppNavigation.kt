package com.example.englishlearningapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.englishlearningapp.features.auth.ui.LoginScreen
import com.example.englishlearningapp.features.auth.ui.RegisterScreen
import com.example.englishlearningapp.features.home.ui.HomeScreen
import com.example.englishlearningapp.features.learn.ui.LearnScreen
import com.example.englishlearningapp.features.profile.ui.ProfileScreen
import com.example.englishlearningapp.features.scan.ui.ScanScreen
import com.example.englishlearningapp.features.vocab.ui.ReviewScreen
import com.example.englishlearningapp.features.vocab.ui.SavedVocabScreen
import com.example.englishlearningapp.features.vocab.ui.CefrDetailScreen
import com.example.englishlearningapp.features.vocab.ui.CefrLevelDetailScreen
import com.example.englishlearningapp.features.vocab.ui.VocabScreen
import com.example.englishlearningapp.features.lesson.ui.TopicListScreen
import com.example.englishlearningapp.features.lesson.ui.LessonListScreen
import com.example.englishlearningapp.features.lesson.ui.LessonDetailScreen
import com.example.englishlearningapp.features.lesson.ui.LessonResultScreen
import com.example.englishlearningapp.features.lesson.viewmodel.LessonViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Learn : Screen("learn")
    object Scan : Screen("scan")
    object Vocab : Screen("vocab")
    object UserTopics : Screen("user_topics")
    object SavedVocab : Screen("saved_vocab")
    object Review : Screen("review")
    object TopicDetail : Screen("topic_detail/{topicId}")
    object CefrDetail : Screen("cefr_detail/{level}") {
        fun createRoute(level: String): String = "cefr_detail/$level"
    }
    object CefrLevelDetail : Screen("cefr_level_detail/{level}") {
        fun createRoute(level: String): String = "cefr_level_detail/$level"
    }
    object UserTopicDetail : Screen("user_topic_detail/{userTopicId}")
    object Flashcard : Screen("flashcard/{topicId}")
    object Profile : Screen("profile")
    // Lesson routes
    object TopicList : Screen("topics")
    object LessonList : Screen("lessons/{topicId}") {
        fun createRoute(topicId: Int): String = "lessons/$topicId"
    }
    object LessonDetail : Screen("lesson-detail/{lessonId}") {
        fun createRoute(lessonId: Int): String = "lesson-detail/$lessonId"
    }
    object LessonResult : Screen("lesson-result")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route,
) {
    val lessonViewModel: LessonViewModel = hiltViewModel()
    val lessonUiState = lessonViewModel.uiState.collectAsState().value

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = hiltViewModel(),
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLessonsClick = {
                    navController.navigate(Screen.TopicList.route)
                },
                onVocabularyClick = {
                    navController.navigate(Screen.Vocab.route)
                },
                onProgressClick = {
                    // Navigate to Progress screen (tạo mới nếu cần)
                    navController.navigate("progress")
                },
                onAiScanClick = {
                    navController.navigate(Screen.Scan.route)
                },
                onSpeakingClick = {
                    // Navigate to Speaking screen (tạo mới nếu cần)
                    navController.navigate(Screen.Learn.route)
                },
                onContinueLearningClick = {
                    navController.navigate(Screen.TopicList.route)
                }
            )
        }
        composable(Screen.Learn.route) { LearnScreen() }
        composable(Screen.Scan.route) { ScanScreen() }
        composable(Screen.Vocab.route) { VocabScreen(navController = navController) }
        composable(Screen.UserTopics.route) { com.example.englishlearningapp.features.usertopic.ui.UserTopicListScreen(navController = navController) }
        composable(Screen.SavedVocab.route) { SavedVocabScreen(navController = navController) }
        composable(Screen.Review.route) { ReviewScreen(navController = navController) }
        composable(Screen.TopicDetail.route) { backStackEntry ->
            val topicIdArg = backStackEntry.arguments?.getString("topicId")
            val topicId = topicIdArg?.toIntOrNull() ?: return@composable
            com.example.englishlearningapp.features.vocab.ui.TopicDetailScreen(
                navController = navController,
                topicId = topicId
            )
        }
        composable(Screen.UserTopicDetail.route) { backStackEntry ->
            val userTopicIdArg = backStackEntry.arguments?.getString("userTopicId")
            val userTopicId = userTopicIdArg?.toIntOrNull() ?: return@composable
            com.example.englishlearningapp.features.usertopic.ui.UserTopicDetailScreen(
                navController = navController,
                userTopicId = userTopicId
            )
        }
        composable(Screen.Flashcard.route) { backStackEntry ->
            val topicIdArg = backStackEntry.arguments?.getString("topicId")
            val topicId = topicIdArg?.toIntOrNull() ?: return@composable
            com.example.englishlearningapp.features.vocab.ui.FlashcardScreen(
                navController = navController,
                topicId = topicId
            )
        }
        composable(Screen.Profile.route) { ProfileScreen() }

        composable(
            route = Screen.CefrDetail.route,
            arguments = listOf(navArgument("level") { type = NavType.StringType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: return@composable
            CefrDetailScreen(
                navController = navController,
                level = level
            )
        }

        composable(
            route = Screen.CefrLevelDetail.route,
            arguments = listOf(navArgument("level") { type = NavType.StringType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: return@composable
            CefrLevelDetailScreen(
                navController = navController,
                level = level
            )
        }

        // --- Lesson Routes ---
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
                navArgument("topicId") { type = NavType.IntType }
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
                navArgument("lessonId") { type = NavType.IntType }
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
                errorMessage = lessonUiState.errorMessage,
                onSelectAnswer = { questionId, answer ->
                    lessonViewModel.selectAnswer(questionId, answer)
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
                        popUpTo(Screen.TopicList.route) { inclusive = true }
                    }
                },
                onProgressClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.TopicList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

