package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.data.remote.NetworkConfig

private val StudyBg = Color(0xFF1A1A1A)
private val StudyCardBg = Color(0xFF2A2A2A)
private val StudyDivider = Color(0xFF3A3A3A)
private val StudyGreen = Color(0xFF4CAF50)

@Composable
fun StudyFlashcardScreen(
    navController: NavController,
    topicId: Int,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val studySession by viewModel.studySession.collectAsState()
    val isRating by viewModel.isRating.collectAsState()
    val audioPlayer = rememberVocabAudioPlayer()

    LaunchedEffect(topicId) {
        viewModel.loadStudySession(topicId)
    }

    if (studySession == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = StudyGreen)
        }
        return
    }

    val studyQueue = remember(studySession) {
        val session = studySession ?: return@remember emptyList<VocabularyResponse>()
        session.newWords + session.dueReviewWords
    }

    if (studyQueue.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text("🎉", fontSize = 72.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Bạn đã học hết từ mới!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Quay lại sau để ôn tập!",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigateUp() },
                    colors = ButtonDefaults.buttonColors(containerColor = StudyGreen)
                ) {
                    Text("Quay lại")
                }
            }
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showRating by remember { mutableStateOf(false) }

    LaunchedEffect(topicId) {
        currentIndex = 0
        isFlipped = false
        showRating = false
    }

    val vocab = studyQueue.getOrNull(currentIndex) ?: run {
        navController.navigateUp()
        return
    }

    LaunchedEffect(currentIndex) {
        audioPlayer.play(vocab.audioUrl, NetworkConfig.BASE_URL, vocab.word)
    }

    val masteryLevel = viewModel.topicProgress.collectAsState().value[vocab.id]?.masteryLevel ?: 0
    val session = studySession ?: return
    val isReviewWord = session.dueReviewWords.any { it.id == vocab.id }

    fun goNext() {
        showRating = false
        isFlipped = false
        if (currentIndex < studyQueue.size - 1) {
            currentIndex++
        } else {
            navController.navigateUp()
        }
    }

    Scaffold(containerColor = StudyBg) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = Color.Gray)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    LinearProgressIndicator(
                        progress = (currentIndex + 1f) / studyQueue.size,
                        modifier = Modifier.fillMaxSize(),
                        color = StudyGreen,
                        trackColor = StudyDivider
                    )
                }

                IconButton(onClick = { }) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isReviewWord) "Ôn tập" else "Từ mới",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.weight(1f))
                MasteryBadge(masteryLevel = masteryLevel)
            }

            Spacer(Modifier.height(12.dp))

            val rotation by animateFloatAsState(
                targetValue = if (isFlipped) 180f else 0f,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                finishedListener = {
                    if (isFlipped) showRating = true
                },
                label = "card_flip"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 16.dp)
                    .graphicsLayer { rotationY = rotation }
                    .clickable(enabled = !isFlipped) { isFlipped = true },
                colors = CardDefaults.cardColors(containerColor = StudyCardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                if (rotation <= 90f) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF3A3A3A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color(0xFF5A5A5A),
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = vocab.word,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            textAlign = TextAlign.Center
                        )
                        if (!vocab.pronunciation.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = vocab.pronunciation,
                                color = Color.Gray,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.FlipToBack,
                                contentDescription = null,
                                tint = StudyGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "Nhấn để lật",
                                color = StudyGreen,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { scaleX = -1f }
                            .padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = vocab.meaning,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (!vocab.exampleSentence.isNullOrBlank()) {
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = StudyDivider)
                            Spacer(Modifier.height(12.dp))
                            Text("Ví dụ:", color = Color.Gray, fontSize = 12.sp)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = vocab.exampleSentence,
                                    color = Color.LightGray,
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.weight(1f)
                                )
                                SpeakerIconButton(
                                    audioUrl = vocab.exampleAudioUrl,
                                    baseUrl = NetworkConfig.BASE_URL,
                                    fallbackText = vocab.exampleSentence,
                                    audioPlayer = audioPlayer,
                                    tint = Color(0xFF7A7A7A),
                                    size = 16.dp
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.FlipToFront,
                                contentDescription = null,
                                tint = StudyGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "Lật lại",
                                color = StudyGreen,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable {
                                    isFlipped = false
                                    showRating = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = showRating,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bạn thuộc từ này ở mức nào?",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.rateVocabulary(vocab.id, 5) { goNext() }
                            },
                            enabled = !isRating,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            border = BorderStroke(1.5.dp, StudyGreen),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF1B3A2D)
                            )
                        ) {
                            Text(
                                text = "Thông thạo",
                                color = StudyGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.rateVocabulary(vocab.id, 3) { goNext() }
                            },
                            enabled = !isRating,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF5A8A6A)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF1A2A20)
                            )
                        ) {
                            Text(
                                text = "Nhớ tạm",
                                color = Color(0xFF5A8A6A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.rateVocabulary(vocab.id, 1) { goNext() }
                        },
                        enabled = !isRating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isRating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Chưa biết",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Thông thạo +5 XP • Nhớ tạm +3 XP • Chưa biết +1 XP",
                        color = Color(0xFF4A4A4A),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun MasteryBadge(masteryLevel: Int) {
    val normalizedLevel = masteryLevel.coerceIn(0, 5)
    val badgeColor = when (normalizedLevel) {
        5 -> StudyGreen
        4 -> Color(0xFF6BCB77)
        3 -> Color(0xFF5A8A6A)
        2 -> Color(0xFF8A8A4A)
        1 -> Color(0xFF6B6B6B)
        else -> Color(0xFF4A4A4A)
    }
    val sweepAngle = if (normalizedLevel == 0) 0f else (normalizedLevel / 5f) * 360f

    Box(
        modifier = Modifier.size(34.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF2A2A2A),
                style = Stroke(width = 5f)
            )
            if (sweepAngle > 0f) {
                drawArc(
                    color = badgeColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 5f, cap = StrokeCap.Round)
                )
            }
        }
        Text(
            text = normalizedLevel.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}