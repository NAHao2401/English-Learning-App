package com.example.englishlearningapp.features.lesson.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.remote.api.response.AnswerOptionResponse
import com.example.englishlearningapp.data.remote.api.response.QuestionResponse
import android.media.MediaPlayer
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    questions: List<QuestionResponse>,
    selectedAnswers: Map<Int, String>,
    isLoading: Boolean,
    errorMessage: String?,
    onSelectAnswer: (Int, String) -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val answeredCount = questions.count {
        !selectedAnswers[it.id].isNullOrBlank()
    }
    val totalQuestions = questions.size
    val progress = if (totalQuestions == 0) 0f else answeredCount / totalQuestions.toFloat()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8F6FF),
            Color(0xFFF6F9FF),
            Color(0xFFFFFFFF)
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lesson Detail",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B2F)
                        )
                        Text(
                            text = "Answer questions and complete the lesson",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF77738A)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF232136)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BottomActionBar(
                answeredCount = answeredCount,
                totalQuestions = totalQuestions,
                isLoading = isLoading,
                onSubmitClick = onSubmitClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
        ) {
            when {
                isLoading && questions.isEmpty() -> LoadingContent()

                errorMessage != null && questions.isEmpty() -> {
                    ErrorContent(message = errorMessage)
                }

                questions.isEmpty() -> EmptyContent()

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 16.dp,
                            bottom = 120.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            LessonDetailHeroCard(
                                totalQuestions = totalQuestions,
                                answeredCount = answeredCount,
                                progress = progress
                            )
                        }

                        if (errorMessage != null) {
                            item {
                                InlineErrorCard(message = errorMessage)
                            }
                        }

                        itemsIndexed(questions) { index, question ->
                            QuestionCard(
                                question = question,
                                index = index,
                                answer = selectedAnswers[question.id].orEmpty(),
                                onSelectAnswer = { value ->
                                    onSelectAnswer(question.id, value)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonDetailHeroCard(
    totalQuestions: Int,
    answeredCount: Int,
    progress: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF6C63FF),
                            Color(0xFF8E7DFF),
                            Color(0xFFB993FF)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
            )

            Column(
                modifier = Modifier.fillMaxWidth(0.86f)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "$answeredCount / $totalQuestions answered",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Keep going, you're making progress",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Answer the questions below. You can work through them one by one and submit when ready.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.90f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${(progress * 100).toInt()}% completed",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: QuestionResponse,
    index: Int,
    answer: String,
    onSelectAnswer: (String) -> Unit
) {
    val isAnswered = answer.isNotBlank()
    val isTextInput = isTextQuestion(question)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(220.dp)
                    .align(Alignment.CenterStart)
                    .background(
                        if (isAnswered) Color(0xFF00A878) else Color(0xFFE3DFFC)
                    )
            )

            Column(
                modifier = Modifier.padding(start = 22.dp, end = 18.dp, top = 18.dp, bottom = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isAnswered) Color(0xFFDDF7EF) else Color(0xFFEAE5FF)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isTextInput) Icons.Rounded.Edit else Icons.Rounded.Quiz,
                            contentDescription = null,
                            tint = if (isAnswered) Color(0xFF00A878) else Color(0xFF7B61FF)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Question ${question.question_order ?: index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF7B778C),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = question.question_text,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF242235),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    StatusBadge(isAnswered = isAnswered)
                }

                Spacer(modifier = Modifier.height(16.dp))

                QuestionTypeChip(
                    label = if (isTextInput) "Text input" else "Multiple choice",
                    color = if (isTextInput) Color(0xFF3A86FF) else Color(0xFF7B61FF),
                    background = if (isTextInput) Color(0xFFE0ECFF) else Color(0xFFEAE5FF)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isTextInput) {
                    ListeningPromptSection(
                        audioUrl = question.audio_url
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TextInputAnswerSection(
                        value = answer,
                        onValueChange = onSelectAnswer
                    )
                } else {
                    if (!question.audio_url.isNullOrBlank()) {
                        ListeningPromptSection(
                            audioUrl = question.audio_url
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    MultipleChoiceAnswerSection(
                        options = question.answer_options,
                        selectedAnswer = answer,
                        onSelectAnswer = onSelectAnswer
                    )
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceAnswerSection(
    options: List<AnswerOptionResponse>,
    selectedAnswer: String,
    onSelectAnswer: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedAnswer == option.option_text

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectAnswer(option.option_text) },
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) Color(0xFFEAE5FF) else Color(0xFFF8F7FC),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) Color(0xFF7B61FF) else Color(0xFFE7E3F0)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF7B61FF) else Color.Transparent
                            )
                            .then(
                                if (!isSelected) {
                                    Modifier.background(Color.Transparent, CircleShape)
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = option.option_text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF2F2B3A),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun TextInputAnswerSection(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        placeholder = {
            Text("Type your answer here")
        },
        singleLine = false,
        minLines = 3,
        maxLines = 5
    )
}

@Composable
private fun StatusBadge(isAnswered: Boolean) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isAnswered) Color(0xFFDDF7EF) else Color(0xFFF5F4FA)
    ) {
        Text(
            text = if (isAnswered) "Answered" else "Pending",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (isAnswered) Color(0xFF00A878) else Color(0xFF7B778C),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuestionTypeChip(
    label: String,
    color: Color,
    background: Color
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BottomActionBar(
    answeredCount: Int,
    totalQuestions: Int,
    isLoading: Boolean,
    onSubmitClick: () -> Unit
) {
    val hasAnyAnswer = answeredCount > 0

    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "$answeredCount / $totalQuestions questions answered",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF242235),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (answeredCount == totalQuestions && totalQuestions > 0) {
                    "You've answered all questions. Submit your lesson now."
                } else {
                    "You can continue answering before submitting."
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF7B778C),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Draft")
                }

                Button(
                    onClick = onSubmitClick,
                    modifier = Modifier.weight(1f),
                    enabled = hasAnyAnswer && !isLoading,
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7B61FF),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (answeredCount == totalQuestions && totalQuestions > 0) {
                            "Complete lesson"
                        } else {
                            "Submit current answers"
                        },
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF6C63FF))
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Loading questions...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6E6A7D)
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No questions yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF242235)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Questions for this lesson will appear here once they are available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6E6A7D)
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        InlineErrorCard(message = message)
    }
}

@Composable
private fun InlineErrorCard(message: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFFEDEC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFC7C2)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB3261E)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8C1D18)
            )
        }
    }
}

private fun isTextQuestion(question: QuestionResponse): Boolean {
    val type = question.question_type.lowercase()

    return type.contains("text") ||
            type.contains("fill") ||
            type.contains("input") ||
            type.contains("typing") ||
            question.answer_options.isEmpty()
}

@Composable
private fun ListeningPromptSection(
    audioUrl: String?
) {
    Column {
        Text(
            text = "Listening prompt",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF666274),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (audioUrl.isNullOrBlank()) {
            AudioUnavailableCard()
        } else {
            AudioPlayerCard(audioUrl = audioUrl)
        }
    }
}

@Composable
private fun AudioUnavailableCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF5F4FA),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color(0xFFE7E3F0)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAE5FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.VolumeUp,
                    contentDescription = null,
                    tint = Color(0xFF7B61FF)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Audio will be available soon",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF242235),
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "This fill-in-the-blank question is designed to support listening practice.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7B778C)
                )
            }
        }
    }
}

@Composable
private fun AudioPlayerCard(
    audioUrl: String
) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPreparing by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(audioUrl) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFEAE5FF),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color(0xFFD8D0FF)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7B61FF))
                    .clickable(enabled = !isPreparing) {
                        if (isPlaying) {
                            mediaPlayer?.pause()
                            isPlaying = false
                        } else {
                            errorMessage = null

                            val existingPlayer = mediaPlayer

                            if (existingPlayer != null) {
                                existingPlayer.start()
                                isPlaying = true
                            } else {
                                isPreparing = true

                                try {
                                    val player = MediaPlayer()

                                    player.setDataSource(audioUrl)
                                    player.setOnPreparedListener {
                                        isPreparing = false
                                        it.start()
                                        isPlaying = true
                                    }
                                    player.setOnCompletionListener {
                                        isPlaying = false
                                    }
                                    player.setOnErrorListener { mp, _, _ ->
                                        isPreparing = false
                                        isPlaying = false
                                        errorMessage = "Cannot play this audio"
                                        mp.release()
                                        mediaPlayer = null
                                        true
                                    }

                                    player.prepareAsync()
                                    mediaPlayer = player
                                } catch (e: Exception) {
                                    isPreparing = false
                                    isPlaying = false
                                    errorMessage = e.message ?: "Cannot play this audio"
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when {
                    isPreparing -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }

                    isPlaying -> {
                        Icon(
                            imageVector = Icons.Rounded.Pause,
                            contentDescription = "Pause audio",
                            tint = Color.White
                        )
                    }

                    else -> {
                        Icon(
                            imageVector = Icons.Rounded.VolumeUp,
                            contentDescription = "Play audio",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isPlaying) "Playing audio" else "Tap to listen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF242235),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = errorMessage ?: "Listen carefully, then type your answer below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (errorMessage == null) Color(0xFF6E6A7D) else Color(0xFFB3261E)
                )
            }

            Icon(
                imageVector = Icons.Rounded.GraphicEq,
                contentDescription = null,
                tint = Color(0xFF7B61FF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}