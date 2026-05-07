package com.example.englishlearningapp.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.englishlearningapp.core.session.SessionManager
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.features.auth.ui.LoginScreen
import com.example.englishlearningapp.features.auth.ui.RegisterScreen
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModel
import com.example.englishlearningapp.features.home.ui.HomeScreen
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModelFactory
import com.example.englishlearningapp.features.lesson.ui.LessonDetailScreen
import com.example.englishlearningapp.features.lesson.ui.LessonListScreen
import com.example.englishlearningapp.features.lesson.ui.LessonResultScreen
import com.example.englishlearningapp.features.lesson.ui.TopicListScreen
import com.example.englishlearningapp.features.lesson.viewmodel.LessonViewModel
import com.example.englishlearningapp.features.progress.ui.ProgressScreen
import com.example.englishlearningapp.features.progress.viewmodel.ProgressViewModel

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
    val userName by appDataStore.userName.collectAsState(initial = "Learner")

    val authUiState by authViewModel.uiState.collectAsState()
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
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                userName = userName.ifBlank { "Learner" },
                onLessonsClick = {
                    navController.navigate(Screen.TopicList.route)
                },
                onProgressClick = {
                    navController.navigate(Screen.Progress.route)
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
                }
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
    }
}
