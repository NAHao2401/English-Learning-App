package com.example.englishlearningapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.englishlearningapp.features.auth.ui.LoginScreen
import com.example.englishlearningapp.features.auth.ui.RegisterScreen
import com.example.englishlearningapp.features.home.ui.HomeScreen
import com.example.englishlearningapp.features.learn.ui.LearnScreen
import com.example.englishlearningapp.features.profile.ui.ProfileScreen
import com.example.englishlearningapp.features.scan.ui.ScanScreen
import com.example.englishlearningapp.features.vocab.ui.VocabScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Learn : Screen("learn")
    object Scan : Screen("scan")
    object Vocab : Screen("vocab")
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
                viewModel = viewModel(),
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
                viewModel = viewModel(),
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
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}

