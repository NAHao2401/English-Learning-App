package com.example.englishlearningapp.features.speaking.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    // Load topics khi vào màn hình lần đầu
    LaunchedEffect(Unit) {
        viewModel.loadTopics()
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Chưa chọn topic → hiện danh sách topic
        uiState.selectedTopic == null -> {
            TopicSelectionScreen(
                topics = uiState.topics,
                errorMessage = uiState.errorMessage,
                onTopicSelected = { viewModel.selectTopic(it) },
                onNavigateBack = onNavigateBack
            )
        }

        // Đã chọn topic → màn luyện nói
        else -> {
            PracticeScreen(
                uiState = uiState,
                onStartListening = { viewModel.startListening() },
                onStopListening = { viewModel.stopListening() },
                onNext = { viewModel.nextSentence() },
                onPrevious = { viewModel.previousSentence() },
                onChangeTopic = { viewModel.loadTopics() }
            )
        }
    }
}

// ── Màn 1: Chọn topic ────────────────────────────────────────────

@Composable
private fun TopicSelectionScreen(
    topics: List<com.example.englishlearningapp.data.remote.dto.SpeakingTopicDto>,
    errorMessage: String?,
    onTopicSelected: (com.example.englishlearningapp.data.remote.dto.SpeakingTopicDto) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TextButton(
            modifier = Modifier.align(Alignment.Start),
            onClick = onNavigateBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Choose a topic",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(topics) { topic ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onTopicSelected(topic) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = topic.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${topic.sentenceCount} sentences",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ── Màn 2: Luyện nói ─────────────────────────────────────────────

@Composable
private fun PracticeScreen(
    uiState: com.example.englishlearningapp.features.speaking.viewmodel.SpeakingUiState,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onChangeTopic: () -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onStartListening()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header: nút đổi topic + số thứ tự câu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onChangeTopic) {
                Text("Topics")
            }
            Text(
                text = "${uiState.currentIndex + 1} / ${uiState.sentences.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Câu mẫu
        uiState.currentSentence?.let { sentence ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = sentence.text,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    sentence.hint?.let { hint ->
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = sentence.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Nút Record
        Button(
            modifier = Modifier.size(72.dp),
            onClick = {
                if (uiState.isListening) {
                    onStopListening()
                } else {
                    val permissionStatus = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    )
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        onStartListening()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.isListening)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(if (uiState.isListening) "Stop" else "Mic")
        }

        if (uiState.isListening) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Listening...",
                color = MaterialTheme.colorScheme.error
            )
        }

        // Kết quả + nút điều hướng
        if (uiState.hasResult) {
            Spacer(modifier = Modifier.height(24.dp))
            ResultSection(
                spokenText = uiState.spokenText,
                score = uiState.score,
                feedback = uiState.feedback
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = uiState.currentIndex > 0
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = onNext,
                    enabled = uiState.currentIndex < uiState.sentences.size - 1
                ) {
                    Text("Next")
                }
            }
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

// ── Result section (giữ nguyên) ───────────────────────────────────

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

    Card(modifier = Modifier.fillMaxWidth()) {
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