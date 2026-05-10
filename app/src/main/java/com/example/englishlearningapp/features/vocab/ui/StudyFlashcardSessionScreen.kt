package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.FlipToFront
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel

private val StudyBg = Color(0xFF1A1A1A)
private val StudyCardBg = Color(0xFF2A2A2A)
private val StudyDivider = Color(0xFF3A3A3A)
private val StudyGreen = Color(0xFF4CAF50)

@Composable
fun StudyFlashcardSessionScreen(
    navController: NavController,
    topicId: Int,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val studySession by viewModel.studySession.collectAsState()
    val studyBatch by viewModel.studyBatch.collectAsState()
    val topicProgress by viewModel.topicProgress.collectAsState()
    val hasMoreWords by viewModel.hasMoreNewWords.collectAsState()
    val isRating by viewModel.isRating.collectAsState()

    var currentBatch by remember { mutableStateOf<List<VocabularyResponse>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showRating by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var ratedCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(topicId) {
        viewModel.loadStudySession(topicId)
        currentBatch = emptyList()
        currentIndex = 0
        isFlipped = false
        showRating = false
        showResult = false
        ratedCount = 0
    }

    LaunchedEffect(studyBatch, showResult) {
        if (!showResult && currentBatch.isEmpty() && studyBatch.isNotEmpty()) {
            currentBatch = studyBatch
        }
    }

    LaunchedEffect(studySession, currentBatch, showResult) {
        if (studySession != null && currentBatch.isEmpty() && studyBatch.isEmpty() && !showResult) {
            showResult = true
        }
    }

    if (studySession == null) {
        LoadingState()
        return
    }

    if (currentBatch.isEmpty() && !showResult) {
        if (studyBatch.isEmpty() && !hasMoreWords) {
            ResultState(
                ratedCount = 0,
                hasMoreWords = false,
                onContinue = {},
                onBack = { navController.navigateUp() }
            )
        } else {
            LoadingState()
        }
        return
    }

    val totalInBatch = currentBatch.size
    val vocab = currentBatch.getOrNull(currentIndex)
    if (vocab == null) {
        ResultState(
            ratedCount = ratedCount,
            hasMoreWords = hasMoreWords,
            onContinue = {
                viewModel.loadTopicDetail(topicId)
                currentBatch = emptyList()
                currentIndex = 0
                isFlipped = false
                showRating = false
                showResult = false
                ratedCount = 0
            },
            onBack = { navController.navigateUp() }
        )
        return
    }

    val masteryLevel = topicProgress[vocab.id]?.masteryLevel ?: 0
    val session = studySession
    val isReviewWord = session?.dueReviewWords?.any { it.id == vocab.id } ?: false

    fun goNext() {
        ratedCount++
        isFlipped = false
        showRating = false
        if (currentIndex < totalInBatch - 1) {
            currentIndex++
        } else {
            showResult = true
        }
    }

    if (showResult) {
        ResultState(
            ratedCount = ratedCount,
            hasMoreWords = hasMoreWords,
            onContinue = {
                viewModel.loadTopicDetail(topicId)
                currentBatch = emptyList()
                currentIndex = 0
                isFlipped = false
                showRating = false
                showResult = false
                ratedCount = 0
            },
            onBack = { navController.navigateUp() }
        )
        return
    }

    Scaffold(containerColor = StudyBg) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        progress = { (currentIndex + 1f) / totalInBatch.toFloat() },
                        modifier = Modifier.fillMaxSize(),
                        color = StudyGreen,
                        trackColor = StudyDivider
                    )
                }

                Spacer(Modifier.size(8.dp))

                Text(
                    text = "${currentIndex + 1}/$totalInBatch",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                IconButton(onClick = { }) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isReviewWord) "Ôn tập" else "Từ mới",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.weight(1f))
                SeedMasteryIcon(masteryLevel = masteryLevel)
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
                    .graphicsLayer { rotationY = rotation }
                    .clickable(enabled = !isFlipped && !isRating) {
                        isFlipped = true
                    },
                colors = CardDefaults.cardColors(containerColor = StudyCardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                if (rotation <= 90f) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
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
                                Icons.Default.FlipToBack,
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
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
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
                            Text(
                                text = vocab.exampleSentence,
                                color = Color.LightGray,
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
                    modifier = Modifier.fillMaxWidth(),
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.rateVocabulary(vocab.id, 5) {
                                    goNext()
                                }
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
                                viewModel.rateVocabulary(vocab.id, 3) {
                                    goNext()
                                }
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
                            viewModel.rateVocabulary(vocab.id, 1) {
                                goNext()
                            }
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
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudyBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = StudyGreen)
            Spacer(Modifier.height(12.dp))
            Text("Đang tải...", color = Color.Gray)
        }
    }
}

@Composable
private fun ResultState(
    ratedCount: Int,
    hasMoreWords: Boolean,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudyBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("🎉", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Bạn đã học $ratedCount từ!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (hasMoreWords) {
                    "Chủ đề này còn từ mới. Tiếp tục học nhé!"
                } else {
                    "Bạn đã học hết từ mới của chủ đề này!"
                },
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

            if (hasMoreWords) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Học tiếp",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                border = BorderStroke(1.5.dp, StudyGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Quay lại",
                    color = StudyGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
