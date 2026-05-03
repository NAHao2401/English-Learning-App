package com.example.englishlearningapp.features.lesson.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.remote.api.response.SubmitLessonResponse

@Composable
fun LessonResultScreen(
    result: SubmitLessonResponse?,
    onRetryClick: () -> Unit,
    onContinueClick: () -> Unit,
    onProgressClick: () -> Unit
) {
    if (result == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("No result")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (result.passed) "Great job!" else "Keep practicing!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Score: ${result.score}%")
        Text("Correct: ${result.correct_count}/${result.total_questions}")
        Text("Wrong: ${result.wrong_count}")
        Text("XP earned: +${result.xp_earned}")
        Text("Completion: ${result.completion_percent}%")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retry")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onProgressClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Progress")
        }
    }
}