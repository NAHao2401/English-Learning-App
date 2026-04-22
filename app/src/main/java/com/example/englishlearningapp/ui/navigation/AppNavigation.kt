package com.example.englishlearningapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.englishlearningapp.ui.home.HomeScreen
import com.example.englishlearningapp.ui.learn.LearnScreen
import com.example.englishlearningapp.ui.profile.ProfileScreen
import com.example.englishlearningapp.ui.scan.ScanScreen
import com.example.englishlearningapp.ui.vocab.VocabScreen

sealed class Screen(val route: String) {
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
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Learn.route) { LearnScreen() }
        composable(Screen.Scan.route) { ScanScreen() }
        composable(Screen.Vocab.route) { VocabScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}

