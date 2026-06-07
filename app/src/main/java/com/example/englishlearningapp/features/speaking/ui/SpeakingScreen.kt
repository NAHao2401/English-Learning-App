package com.example.englishlearningapp.features.speaking.ui

import android.Manifest
import android.R.attr.navigationIcon
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.automirrored.rounded.ArrowBack

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingSentenceItem
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingUiState
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingViewModel

@Composable
fun SpeakingScreen(
    viewModel: SpeakingViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadTopics() }

    when {
        uiState.isLoading -> LoadingScreen()

        // Chưa chọn topic
        uiState.selectedTopic == null -> TopicScreen(
            topics = uiState.topics,
            errorMessage = uiState.errorMessage,
            onTopicSelected = { viewModel.selectTopic(it) },
            onNavigateBack = onBackClick
        )

        // Đã chọn topic → màn luyện nói
        else -> PracticeScreen(
            uiState = uiState,
            onStartListening = { viewModel.startListening() },
            onStopListening = { viewModel.stopListening() },
            onNext = { viewModel.nextSentence() },
            onPrevious = { viewModel.previousSentence() },
            onBackToTopics = { viewModel.backToTopics() }
        )
    }
}

// ── Loading ───────────────────────────────────────────────────────────────────

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// ── Chọn topic ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicScreen(
    topics: List<String>,
    errorMessage: String?,
    onTopicSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Speaking",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Choose a topic",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },

                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (topics.isEmpty() && errorMessage == null) {
                Text(
                    text = "No topics available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(topics) { topic ->
                    Card(
                        onClick = { onTopicSelected(topic) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 16.dp
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = topic.replace("_", " ")
                                    .split(" ")
                                    .joinToString(" ") {
                                        it.replaceFirstChar { c ->
                                            c.uppercase()
                                        }
                                    },
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "→",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
// ── Luyện nói ─────────────────────────────────────────────────────────────────

@Composable
private fun PracticeScreen(
    uiState: SpeakingUiState,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onBackToTopics: () -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) onStartListening() }

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
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Header ─────────────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackToTopics) { Text("Topics") }

            Text(
                text = "${uiState.currentIndex + 1} / ${uiState.sentences.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                fontWeight = FontWeight.SemiBold
            )
        }

        // ── Progress bar ───────────────────────────────────────────────
        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier.fillMaxWidth()
        )

        // ── Câu mẫu ────────────────────────────────────────────────────
        uiState.currentSentence?.let { sentence ->
            SentenceCard(sentence = sentence)
        }

        // ── Nút record ─────────────────────────────────────────────────
        Button(
            onClick = {
                if (uiState.isListening) {
                    onStopListening()
                } else {
                    if (ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        onStartListening()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            },
            modifier = Modifier.size(72.dp),
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
            Text(
                text = "Listening...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        // ── Kết quả ────────────────────────────────────────────────────
        if (uiState.hasResult) {
            ResultCard(uiState = uiState)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = uiState.currentIndex > 0
                ) { Text("Previous") }

                Button(
                    onClick = onNext,
                    enabled = uiState.currentIndex < uiState.sentences.size - 1
                ) { Text("Next") }
            }

            if (uiState.isFinished) {
                Text(
                    text = "You have completed all sentences in this topic!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ── Lỗi ────────────────────────────────────────────────────────
        uiState.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Result section (giữ nguyên) ───────────────────────────────────

@Composable
private fun SentenceCard(sentence: SpeakingSentenceItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = sentence.sentence,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            sentence.translation?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
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

@Composable
private fun ResultCard(uiState: SpeakingUiState) {
    val scoreColor = when {
        uiState.score >= 70 -> Color(0xFF1D9E75)
        uiState.score >= 50 -> Color(0xFFBA7517)
        else                 -> Color(0xFFE24B4A)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("You said:", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("\"${uiState.spokenText}\"",
                style = MaterialTheme.typography.bodyLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.score}%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = scoreColor,
                    fontWeight = FontWeight.Bold
                )
                Text(uiState.feedback,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}