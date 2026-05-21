package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.englishlearningapp.data.remote.NetworkConfig
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse

private val VocabCardBg = Color.White
private val VocabTextPrimary = Color(0xFF1D1B2F)
private val VocabTextSecondary = Color(0xFF77738A)
private val VocabTextMuted = Color(0xFF9A97A8)
private val VocabDivider = Color(0xFFE6E2F2)

/**
 * Reusable vocabulary card with expand/collapse for showing meaning, example, etc.
 * Used in SavedVocabScreen, UserTopicDetailScreen, etc.
 */
@Composable
fun VocabExpandableCard(
    vocab: VocabularyResponse,
    savedVocabIds: Set<Int>,
    audioPlayer: VocabAudioPlayer,
    showSaveAction: Boolean = true,
    onRemoveFromTopic: (() -> Unit)? = null
) {
    var expanded by remember(vocab.id) { mutableStateOf(false) }
    var showSaveSheet by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "expand_arrow"
    )

    val accentColor = vocabAccent()
    val primaryTextColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    val mutedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val dividerColor = vocabDividerColor()

    Card(
        colors = CardDefaults.cardColors(containerColor = vocabCardContainer()),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(vocab.word, color = primaryTextColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (!vocab.pronunciation.isNullOrBlank()) {
                        Text(vocab.pronunciation, color = secondaryTextColor, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                    }
                }

                if (showSaveAction) {
                    IconButton(onClick = { showSaveSheet = true }) {
                        Icon(
                            if (savedVocabIds.contains(vocab.id)) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (savedVocabIds.contains(vocab.id)) accentColor else mutedTextColor
                        )
                    }
                } else if (onRemoveFromTopic != null) {
                    IconButton(onClick = { onRemoveFromTopic() }, modifier = Modifier.size(40.dp)) {
                        Text(
                            text = "-",
                            color = Color(0xFFFF3B30),
                            fontSize = 28.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = mutedTextColor,
                    modifier = Modifier.rotate(rotation)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = dividerColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text("Nghĩa  ", color = secondaryTextColor, fontSize = 12.sp)
                        Text(vocab.meaning, color = primaryTextColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    if (!vocab.exampleSentence.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            Text("Ví dụ  ", color = secondaryTextColor, fontSize = 12.sp)
                            Text(vocab.exampleSentence, color = secondaryTextColor, fontSize = 13.sp, fontStyle = FontStyle.Italic)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!vocab.difficulty.isNullOrBlank()) {
                            Badge(containerColor = levelCodeColor(vocab.difficulty).copy(alpha = 0.2f)) {
                                Text(vocab.difficulty, color = levelCodeColor(vocab.difficulty), fontSize = 10.sp)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        SpeakerIconButton(
                            audioUrl = vocab.audioUrl,
                            baseUrl = NetworkConfig.BASE_URL,
                            fallbackText = vocab.word,
                            audioPlayer = audioPlayer,
                            tint = accentColor,
                            size = 20.dp
                        )
                    }
                }
            }
        }
    }

    if (showSaveAction && showSaveSheet) {
        SaveToTopicBottomSheet(
            vocab = vocab,
            onDismiss = { showSaveSheet = false }
        )
    }
}

@Composable
fun SeedMasteryIcon(
    masteryLevel: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            val segmentSweep = 60f
            val gapSweep = 12f

            repeat(5) { i ->
                val startAngle = -90f + i * (segmentSweep + gapSweep)
                val isFilled = i < masteryLevel

                drawArc(
                    color = if (isFilled) Color(0xFF4CAF50) else Color(0xFFD2CEDF),
                    startAngle = startAngle,
                    sweepAngle = segmentSweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Text(
            text = if (masteryLevel >= 5) "🌻" else "🌱",
            fontSize = if (masteryLevel >= 5) 22.sp else 20.sp
        )
    }
}

@Composable
fun VocabRowWithSeed(
    vocab: VocabularyResponse,
    masteryLevel: Int,
    audioPlayer: VocabAudioPlayer,
    onSaveClick: () -> Unit,
    useLeafIcon: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "expand_arrow"
    )

    val accentColor = vocabAccent()
    val primaryTextColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    val mutedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val dividerColor = vocabDividerColor()

    Card(
        colors = CardDefaults.cardColors(containerColor = vocabCardContainer()),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (useLeafIcon) {
                    LearnedSeedIconLocal(masteryLevel = masteryLevel)
                } else {
                    SeedMasteryIcon(masteryLevel = masteryLevel)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vocab.word,
                            color = if (masteryLevel > 0) accentColor else primaryTextColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        SpeakerIconButton(
                            audioUrl = vocab.audioUrl,
                            baseUrl = NetworkConfig.BASE_URL,
                            fallbackText = vocab.word,
                            audioPlayer = audioPlayer,
                            tint = if (masteryLevel > 0) accentColor else primaryTextColor,
                            size = 18.dp
                        )
                    }

                    if (!vocab.pronunciation.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(text = vocab.pronunciation, color = secondaryTextColor, fontSize = 13.sp, fontStyle = FontStyle.Italic)
                    }
                }

                IconButton(onClick = onSaveClick) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = "Lưu từ", tint = mutedTextColor)
                }
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = mutedTextColor,
                    modifier = Modifier.rotate(rotation)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 74.dp, end = 14.dp, bottom = 14.dp)) {
                    HorizontalDivider(color = dividerColor)
                    Spacer(Modifier.height(8.dp))
                    Text(text = vocab.meaning, color = primaryTextColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    if (!vocab.exampleSentence.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Row {
                            Text(
                                text = vocab.exampleSentence,
                                color = secondaryTextColor,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.weight(1f)
                            )
                            SpeakerIconButton(
                                audioUrl = vocab.exampleAudioUrl,
                                baseUrl = NetworkConfig.BASE_URL,
                                fallbackText = vocab.exampleSentence,
                                audioPlayer = audioPlayer,
                                tint = mutedTextColor,
                                size = 16.dp
                            )
                        }
                    }

                    if (masteryLevel > 0) {
                        Spacer(Modifier.height(8.dp))
                        val masteryLabels = mapOf(1 to "Chưa biết", 2 to "Mới học", 3 to "Nhớ tạm", 4 to "Nhớ lâu", 5 to "Thông thạo")
                        Badge(containerColor = accentColor.copy(alpha = 0.2f)) {
                            Text(masteryLabels[masteryLevel] ?: "", color = accentColor, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
