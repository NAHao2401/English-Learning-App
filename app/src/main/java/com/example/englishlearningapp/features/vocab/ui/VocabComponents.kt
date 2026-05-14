package com.example.englishlearningapp.features.vocab.ui

import com.example.englishlearningapp.data.remote.NetworkConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.features.vocab.ui.SaveToTopicBottomSheet
import com.example.englishlearningapp.features.vocab.ui.levelCodeColor

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

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(vocab.word, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (!vocab.pronunciation.isNullOrBlank()) Text(vocab.pronunciation, color = Color.Gray, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                }

                if (showSaveAction) {
                    IconButton(onClick = { showSaveSheet = true }) {
                        Icon(
                            if (savedVocabIds.contains(vocab.id)) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (savedVocabIds.contains(vocab.id)) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                } else {
                    IconButton(onClick = { onRemoveFromTopic?.invoke() }, modifier = Modifier.size(40.dp)) {
                        Text(
                            text = "−",
                            color = Color(0xFFFF3B30),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF3A3A3A))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text("Nghĩa  ", color = Color.Gray, fontSize = 12.sp)
                        Text(vocab.meaning, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    if (!vocab.exampleSentence.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            Text("Ví dụ  ", color = Color.Gray, fontSize = 12.sp)
                            Text(vocab.exampleSentence, color = Color.LightGray, fontSize = 13.sp, fontStyle = FontStyle.Italic)
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
                            tint = Color(0xFF4CAF50),
                            size = 20.dp
                        )
                    }
                }
            }
        }
    }

    // Show bottom sheet
    if (showSaveAction && showSaveSheet) {
        SaveToTopicBottomSheet(
            vocab = vocab,
            onDismiss = { showSaveSheet = false }
        )
    }
}







