package com.example.englishlearningapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.englishlearningapp.features.auth.ui.LoginScreen
import com.example.englishlearningapp.features.auth.ui.RegisterScreen
import com.example.englishlearningapp.features.home.ui.HomeScreen
import com.example.englishlearningapp.features.learn.ui.LearnScreen
import com.example.englishlearningapp.features.profile.ui.ProfileScreen
import com.example.englishlearningapp.features.scan.ui.ScanScreen
import com.example.englishlearningapp.features.vocab.ui.ReviewScreen
import com.example.englishlearningapp.features.vocab.ui.SavedVocabScreen
import com.example.englishlearningapp.features.vocab.ui.VocabScreen
import androidx.hilt.navigation.compose.hiltViewModel

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
    object UserTopicDetail : Screen("user_topic_detail/{userTopicId}")
    object Flashcard : Screen("flashcard/{topicId}")
    object Profile : Screen("profile")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route,
) {
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

        composable(Screen.Home.route) { HomeScreen() }
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
    }
}

