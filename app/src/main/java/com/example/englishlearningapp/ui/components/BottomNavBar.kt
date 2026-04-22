package com.example.englishlearningapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.englishlearningapp.ui.navigation.Screen
import com.example.englishlearningapp.ui.theme.Green500
import com.example.englishlearningapp.ui.theme.GreenAccent
import com.example.englishlearningapp.ui.theme.InactiveIcon
import com.example.englishlearningapp.ui.theme.NavBarBackground

private data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: @Composable () -> Unit,
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem(
            label = "Home",
            route = Screen.Home.route,
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
        ),
        BottomNavItem(
            label = "Learn",
            route = Screen.Learn.route,
            icon = { Icon(Icons.Filled.MenuBook, contentDescription = "Learn") },
        ),
        BottomNavItem(
            label = "Scan",
            route = Screen.Scan.route,
            icon = {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Scan",
                    tint = GreenAccent,
                )
            },
        ),
        BottomNavItem(
            label = "Vocab",
            route = Screen.Vocab.route,
            icon = { Icon(Icons.Filled.Style, contentDescription = "Vocab") },
        ),
        BottomNavItem(
            label = "Profile",
            route = Screen.Profile.route,
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
        ),
    )

    NavigationBar(containerColor = NavBarBackground) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = item.icon,
                label = { Text(text = item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Green500,
                    selectedTextColor = Green500,
                    unselectedIconColor = InactiveIcon,
                    unselectedTextColor = InactiveIcon,
                    indicatorColor = NavBarBackground,
                ),
            )
        }
    }
}

