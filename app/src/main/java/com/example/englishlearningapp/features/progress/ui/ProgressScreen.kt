package com.example.englishlearningapp.features.progress.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.remote.api.response.ProgressSummaryResponse

@Composable
fun ProgressScreen(
    summary: ProgressSummaryResponse?,
    isLoading: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextButton(onClick = onBackClick) {
            Text("← Back")
        }

        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (summary != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Level: ${summary.current_level}")
                    Text("Total XP: ${summary.total_xp}")
                    Text("Streak: ${summary.streak_count} days")
                    Text("Study days: ${summary.study_days}")
                    Text("Completed lessons: ${summary.completed_lessons}/${summary.total_lessons}")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Overall progress: ${summary.completion_percent}%")

                    LinearProgressIndicator(
                        progress = {
                            summary.completion_percent / 100f
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}