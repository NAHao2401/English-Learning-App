package com.example.englishlearningapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.englishlearningapp.ui.theme.DarkBackground
import com.example.englishlearningapp.ui.theme.TextPrimary

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Profile Screen",
            color = TextPrimary,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
