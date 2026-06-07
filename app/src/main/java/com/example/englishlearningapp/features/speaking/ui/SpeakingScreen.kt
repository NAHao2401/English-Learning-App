package com.example.englishlearningapp.features.speaking.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingSentenceItem
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingTopicItem
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingUiState
import com.example.englishlearningapp.features.speaking.viewmodel.SpeakingViewModel

@Composable
fun SpeakingScreen(
    viewModel: SpeakingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTopics()
    }

    when {
        uiState.isLoading && uiState.selectedTopic == null -> LoadingScreen()
        uiState.selectedTopic == null -> TopicScreen(
            topics = uiState.topics,
            errorMessage = uiState.errorMessage,
            onTopicSelected = viewModel::selectTopic,
            onNavigateBack = onNavigateBack
        )
        else -> PracticeScreen(
            uiState = uiState,
            onStartListening = viewModel::startListening,
            onStopListening = viewModel::stopListening,
            onNext = viewModel::nextSentence,
            onPrevious = viewModel::previousSentence,
            onBackToTopics = viewModel::backToTopics
        )
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicScreen(
    topics: List<SpeakingTopicItem>,
    errorMessage: String?,
    onTopicSelected: (SpeakingTopicItem) -> Unit,
    onNavigateBack: () -> Unit
) {
    val backgroundBrush = speakingScreenBackgroundBrush()

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
                .background(backgroundBrush)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            SpeakingHeroCard(topicCount = topics.size)
            Spacer(modifier = Modifier.height(18.dp))

            errorMessage?.let {
                InlineSpeakingError(message = it)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (topics.isEmpty() && errorMessage == null) {
                EmptySpeakingTopics()
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(topics, key = { it.id }) { topic ->
                    SpeakingTopicCard(
                        topic = topic,
                        visual = speakingTopicVisual(topic),
                        onClick = { onTopicSelected(topic) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeakingHeroCard(topicCount: Int) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF5F6CF2), Color(0xFF22B8A8))
                    )
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Practice speaking",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$topicCount focused topics ready",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.86f)
                )
            }
        }
    }
}

@Composable
private fun SpeakingTopicCard(
    topic: SpeakingTopicItem,
    visual: SpeakingTopicVisual,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(visual.softAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.accent,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatTopic(topic.name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = visual.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(visual.softAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Open topic",
                    tint = visual.accent
                )
            }
        }
    }
}

@Composable
private fun InlineSpeakingError(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptySpeakingTopics() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
        )
    ) {
        Text(
            text = "No topics available.",
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

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
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onStartListening()
    }
    val backgroundBrush = speakingScreenBackgroundBrush()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PracticeTopBar(
            uiState = uiState,
            onBackToTopics = onBackToTopics
        )

        if (uiState.isLoading) {
            LoadingPracticeCard()
        } else {
            uiState.currentSentence?.let { sentence ->
                SentenceCard(sentence = sentence)
            } ?: Text(
                text = "No sentences available for this topic.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        SpeakingMicButton(
            isListening = uiState.isListening,
            enabled = uiState.currentSentence != null && !uiState.isLoading,
            onClick = {
                if (uiState.isListening) {
                    onStopListening()
                } else if (
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    onStartListening()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        )

        if (uiState.isListening) {
            Text(
                text = "Listening to your pronunciation...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (uiState.hasResult) {
            ResultCard(uiState = uiState)

            val isLastSentence = uiState.currentIndex == uiState.sentences.lastIndex

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = uiState.currentIndex > 0
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = {
                        if (isLastSentence) {
                            onBackToTopics()
                        } else {
                            onNext()
                        }
                    },
                    enabled = isLastSentence || uiState.currentIndex < uiState.sentences.lastIndex
                ) {
                    Text(if (isLastSentence) "Hoàn thành" else "Next")
                }
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

@Composable
private fun PracticeTopBar(
    uiState: SpeakingUiState,
    onBackToTopics: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToTopics,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Card(
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                )
            ) {
                Text(
                    text = "${uiState.currentIndex + 1} / ${uiState.sentences.size}",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF5F6CF2), Color(0xFF22B8A8))
                        )
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Mic,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = formatTopic(uiState.selectedTopic?.name ?: "Speaking"),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Say the sentence out loud",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.84f)
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { uiState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.24f)
                )
            }
        }
    }
}

@Composable
private fun LoadingPracticeCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun SpeakingMicButton(
    isListening: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(92.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) Color(0xFFE24B4A) else Color(0xFF5F6CF2),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = if (isListening) Icons.Rounded.Pause else Icons.Rounded.Mic,
                contentDescription = if (isListening) "Stop listening" else "Start speaking",
                modifier = Modifier.size(34.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (isListening) "Tap to stop" else "Tap to speak",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SentenceCard(sentence: SpeakingSentenceItem) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sentence",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Card(
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                    )
                ) {
                    Text(
                        text = sentence.difficulty.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = sentence.sentence,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            sentence.translation?.let {
                if (it.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultCard(uiState: SpeakingUiState) {
    val scoreColor = when {
        uiState.score >= 70 -> Color(0xFF1D9E75)
        uiState.score >= 50 -> Color(0xFFBA7517)
        else -> Color(0xFFE24B4A)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your pronunciation",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.feedback,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(scoreColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${uiState.score}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = scoreColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
                    .padding(14.dp)
            ) {
                Text(
                    text = "\"${uiState.spokenText}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class SpeakingTopicVisual(
    val icon: ImageVector,
    val accent: Color,
    val softAccent: Color,
    val description: String
)

@Composable
private fun speakingScreenBackgroundBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )
}

private fun speakingTopicVisual(topic: SpeakingTopicItem): SpeakingTopicVisual {
    return when (topic.name.lowercase()) {
        "food" -> SpeakingTopicVisual(
            icon = Icons.Rounded.Restaurant,
            accent = Color(0xFFE35D5B),
            softAccent = Color(0xFFFFE7E2),
            description = "Order meals, ask meanings, and answer quick food questions"
        )
        "travel" -> SpeakingTopicVisual(
            icon = Icons.Rounded.FlightTakeoff,
            accent = Color(0xFF4F74E8),
            softAccent = Color(0xFFE7ECFF),
            description = "Practice airport, hotel, and direction conversations"
        )
        "daily_life", "daily life" -> SpeakingTopicVisual(
            icon = Icons.Rounded.Home,
            accent = Color(0xFF17A48B),
            softAccent = Color(0xFFE1F7F1),
            description = "Build confidence with everyday questions and replies"
        )
        "work" -> SpeakingTopicVisual(
            icon = Icons.Rounded.Work,
            accent = Color(0xFF7B61D9),
            softAccent = Color(0xFFEDE7FF),
            description = "Speak clearly in meetings, tasks, and office situations"
        )
        "health" -> SpeakingTopicVisual(
            icon = Icons.Rounded.Favorite,
            accent = Color(0xFFE04E83),
            softAccent = Color(0xFFFFE5EF),
            description = "Describe symptoms, habits, and simple care needs"
        )
        "shopping" -> SpeakingTopicVisual(
            icon = Icons.Rounded.ShoppingBag,
            accent = Color(0xFFC77916),
            softAccent = Color(0xFFFFEFD6),
            description = "Ask prices, compare items, and handle store phrases"
        )
        else -> SpeakingTopicVisual(
            icon = Icons.Rounded.Mic,
            accent = Color(0xFF5F6CF2),
            softAccent = Color(0xFFE8EAFF),
            description = "Practice short speaking prompts for this topic"
        )
    }
}

private fun formatTopic(topic: String): String {
    return topic.replace("_", " ")
        .split(" ")
        .joinToString(" ") { part ->
            part.replaceFirstChar { char -> char.uppercase() }
        }
}
