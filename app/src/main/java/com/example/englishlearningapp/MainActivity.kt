package com.example.englishlearningapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.englishlearningapp.core.notification.ReviewReminderManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.navigation.AppNavGraph
import com.example.englishlearningapp.navigation.Screen
import com.example.englishlearningapp.ui.theme.EnglishLearningAppTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        RetrofitClient.init(applicationContext)
        requestNotificationPermission()

        val appDataStore = AppDataStore(applicationContext)

        setContent {
            val isDarkMode by appDataStore.isDarkMode.collectAsState(initial = false)

            EnglishLearningAppTheme(
                darkTheme = isDarkMode,
                dynamicColor = false
            ) {
                val isLoggedIn by appDataStore.isLoggedIn.collectAsState(initial = false)

                AppNavGraph(
                    context = applicationContext,
                    startDestination = if (isLoggedIn) {
                        Screen.Home.route
                    } else {
                        Screen.Login.route
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ReviewReminderManager(applicationContext).setAppForeground(true)
    }

    override fun onStop() {
        ReviewReminderManager(applicationContext).setAppForeground(false)
        super.onStop()
    }

    private fun requestNotificationPermission() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
