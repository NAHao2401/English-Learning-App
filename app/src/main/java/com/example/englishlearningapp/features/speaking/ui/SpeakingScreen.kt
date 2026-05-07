package com.example.englishlearningapp.features.speaking.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingViewModel

@Composable
fun SpeakingScreen(
    viewModel: SpeakingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            modifier = Modifier.align(Alignment.Start),
            onClick = onNavigateBack
        ) {
            Text(text = "Back")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                text = uiState.sampleSentence,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            modifier = Modifier.size(72.dp),
            onClick = {
                if (uiState.isListening) {
                    viewModel.stopListening()
                } else {
                    val permissionStatus = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    )

                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        viewModel.startListening()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.isListening) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(text = if (uiState.isListening) "Stop" else "Mic")
        }

        if (uiState.isListening) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Listening...",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (uiState.hasResult) {
            Spacer(modifier = Modifier.height(24.dp))
            ResultSection(
                spokenText = uiState.spokenText,
                score = uiState.score,
                feedback = uiState.feedback
            )
        }

        uiState.errorMessage?.let { errorMessage ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ResultSection(
    spokenText: String,
    score: Int,
    feedback: String
) {
    val scoreColor = when {
        score >= 70 -> Color(0xFF1D9E75)
        score >= 50 -> Color(0xFFBA7517)
        else -> Color(0xFFE24B4A)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "You said:",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = "\"$spokenText\"",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = feedback,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
