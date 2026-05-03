package com.example.englishlearningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.features.auth.ui.LoginScreen
import com.example.englishlearningapp.features.auth.ui.RegisterScreen
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModel
import com.example.englishlearningapp.features.home.ui.HomeScreen
import com.example.englishlearningapp.features.lesson.ui.LessonDetailScreen
import com.example.englishlearningapp.features.lesson.ui.LessonListScreen
import com.example.englishlearningapp.features.lesson.ui.LessonResultScreen
import com.example.englishlearningapp.features.lesson.ui.TopicListScreen
import com.example.englishlearningapp.features.lesson.viewmodel.LessonViewModel
import com.example.englishlearningapp.features.progress.ui.ProgressScreen
import com.example.englishlearningapp.features.progress.viewmodel.ProgressViewModel
import com.example.englishlearningapp.ui.theme.EnglishLearningAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        RetrofitClient.init(applicationContext)
        val authViewModel = AuthViewModel(this)

        setContent {
            EnglishLearningAppTheme {
                var showRegister by remember { mutableStateOf(false) }
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    var currentScreen by remember { mutableStateOf("home") }
                    var selectedTopicId by remember { mutableIntStateOf(0) }
                    var selectedLessonId by remember { mutableIntStateOf(0) }

                    val lessonViewModel: LessonViewModel = viewModel()
                    val progressViewModel: ProgressViewModel = viewModel()

                    when (currentScreen) {
                        "home" -> {
                            HomeScreen(
                                onLessonsClick = {
                                    currentScreen = "topics"
                                },
                                onProgressClick = {
                                    progressViewModel.loadProgressSummary()
                                    currentScreen = "progress"
                                }
                            )
                        }

                        "topics" -> {
                            LaunchedEffect(Unit) {
                                lessonViewModel.loadTopics()
                            }

                            TopicListScreen(
                                topics = lessonViewModel.topics,
                                isLoading = lessonViewModel.isLoading,
                                errorMessage = lessonViewModel.errorMessage,
                                onTopicClick = { topicId ->
                                    selectedTopicId = topicId
                                    lessonViewModel.loadLessonsByTopic(topicId)
                                    currentScreen = "lessons"
                                },
                                onBackClick = {
                                    currentScreen = "home"
                                }
                            )
                        }

                        "lessons" -> {
                            LessonListScreen(
                                lessons = lessonViewModel.lessons,
                                isLoading = lessonViewModel.isLoading,
                                errorMessage = lessonViewModel.errorMessage,
                                onLessonClick = { lessonId ->
                                    selectedLessonId = lessonId
                                    lessonViewModel.loadQuestions(lessonId)
                                    currentScreen = "lesson_detail"
                                },
                                onBackClick = {
                                    currentScreen = "topics"
                                }
                            )
                        }

                        "lesson_detail" -> {
                            LessonDetailScreen(
                                questions = lessonViewModel.questions,
                                selectedAnswers = lessonViewModel.selectedAnswers,
                                isLoading = lessonViewModel.isLoading,
                                errorMessage = lessonViewModel.errorMessage,
                                onSelectAnswer = { questionId, answer ->
                                    lessonViewModel.selectAnswer(questionId, answer)
                                },
                                onSubmitClick = {
                                    lessonViewModel.submitLesson(selectedLessonId) {
                                        currentScreen = "lesson_result"
                                    }
                                },
                                onBackClick = {
                                    currentScreen = "lessons"
                                }
                            )
                        }

                        "lesson_result" -> {
                            LessonResultScreen(
                                result = lessonViewModel.submitResult,
                                onRetryClick = {
                                    lessonViewModel.loadQuestions(selectedLessonId)
                                    currentScreen = "lesson_detail"
                                },
                                onContinueClick = {
                                    lessonViewModel.loadLessonsByTopic(selectedTopicId)
                                    currentScreen = "lessons"
                                },
                                onProgressClick = {
                                    progressViewModel.loadProgressSummary()
                                    currentScreen = "progress"
                                }
                            )
                        }

                        "progress" -> {
                            ProgressScreen(
                                summary = progressViewModel.summary,
                                isLoading = progressViewModel.isLoading,
                                errorMessage = progressViewModel.errorMessage,
                                onBackClick = {
                                    currentScreen = "home"
                                }
                            )
                        }
                    }
                } else {
                    if (showRegister) {
                        RegisterScreen(
                            viewModel = authViewModel,
                            onNavigateToLogin = { showRegister = false },
                            onRegisterSuccess = { showRegister = false }
                        )
                    } else {
                        LoginScreen(
                            viewModel = authViewModel,
                            onNavigateToRegister = { showRegister = true },
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}