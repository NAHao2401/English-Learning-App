package com.example.englishlearningapp.features.vocab.ui

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
// java.util.Locale not required here

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    navController: NavController,
    topicId: Int,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val topicsWithCount by viewModel.topics.collectAsState()
    val topicVocabs by viewModel.currentTopicVocabs.collectAsState()
    val difficultyFilter by viewModel.difficultyFilter.collectAsState()
    val savedVocabs by viewModel.savedVocabs.collectAsState()
    val savedIds by remember(savedVocabs) { derivedStateOf { savedVocabs.map { it.id }.toSet() } }
    val topicWithCount: TopicWithCount? = topicsWithCount.find { it.topic.id == topicId }
    val topic = topicWithCount?.topic
    var selectedVocab by remember { mutableStateOf<VocabularyResponse?>(null) }

    val context = LocalContext.current
    val tts = remember { TextToSpeech(context, null) }
    DisposableEffect(Unit) { onDispose { tts.shutdown() } }

    LaunchedEffect(topicId) {
        viewModel.setDifficultyFilter(null)
        viewModel.selectTopic(topicId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(topic?.name ?: "Topic") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButtonWithColor(text = "🃏 Flashcard", onClick = { navController.navigate("flashcard/$topicId") })
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1A1A1A))
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = levelBgColor(topic?.level)),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ) {
                // Determine display icon vs color
                                        val topicIconText = remember(topic?.iconUrl) { topic?.iconUrl?.takeUnless { it.startsWith("#") } ?: "📚" }
                                        val topicAccentColor = remember(topic?.iconUrl, topic?.level) {
                                            topic?.iconUrl
                                                ?.takeIf { it.startsWith("#") }
                                                ?.let { Color(android.graphics.Color.parseColor(it)) }
                                                ?: levelCodeColor(topic?.level)
                                        }

                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (topic?.iconUrl?.startsWith("#") == true) {
                        // show colored box for hex color codes
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(topicAccentColor, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            // intentionally empty: color is the visual element
                        }
                    } else {
                        Text(topicIconText, fontSize = 48.sp)
                    }

                    Spacer(modifier = Modifier.size(16.dp))
                    Column {
                        Text(topic?.name ?: "", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(topic?.description ?: "", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge(containerColor = Color.White.copy(alpha = 0.2f)) {
                                Text(topic?.level ?: "", color = Color.White, fontSize = 11.sp)
                            }
                            Badge(containerColor = Color.White.copy(alpha = 0.2f)) {
                                Text("${topicVocabs.size} từ", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Difficulty chips
            val filterOptions = listOf<String?>(null, "A0", "A1", "A2", "B1", "B2")
            val filterLabels = listOf("Tất cả", "A0", "A1", "A2", "B1", "B2")
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions.zip(filterLabels)) { (value, label) ->
                    FilterChip(selected = difficultyFilter == value, onClick = { viewModel.setDifficultyFilter(value) }, label = { Text(label) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF4CAF50), selectedLabelColor = Color.White, containerColor = Color(0xFF2A2A2A), labelColor = Color.Gray))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                topicVocabs.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có từ vựng nào", color = Color.Gray)
                }

                else -> LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(items = topicVocabs, key = { it.id }, contentType = { "vocab" }) { vocab: VocabularyEntity ->
                        WordItem(
                            vocab = vocab,
                            isSaved = savedIds.contains(vocab.id),
                            onSaveClick = {
                                selectedVocab = vocab.toVocabularyResponse()
                            },
                            tts = tts
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            selectedVocab?.let { vocabToSave ->
                SaveToTopicBottomSheet(
                    vocab = vocabToSave,
                    onDismiss = { selectedVocab = null },
                    onSaved = { viewModel.setSaved(vocabToSave.id, true) }
                )
            }
        }
    }
}

@Composable
private fun TextButtonWithColor(text: String, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        Text(text, color = Color(0xFF4CAF50))
    }
}

@Composable
fun WordItem(
    vocab: VocabularyEntity,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    tts: TextToSpeech
) {
    var expanded by remember(vocab.id) { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, animationSpec = tween(durationMillis = 300))

    // Always use the latest onSaveClick callback
    val onSaveClickState = rememberUpdatedState(onSaveClick)

    // Formatings or expensive computations can be remembered
    val displayPron = remember(vocab.pronunciation) { vocab.pronunciation }

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
                    if (!displayPron.isNullOrBlank()) Text(displayPron, color = Color.Gray, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                }

                IconButton(onClick = {
                    onSaveClickState.value()
                }) {
                    Icon(
                        if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (isSaved) Color(0xFF4CAF50) else Color.Gray
                    )
                }

                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.rotate(rotation))
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.HorizontalDivider(color = Color(0xFF3A3A3A))
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
                        IconButton(onClick = { try { tts.speak(vocab.word, TextToSpeech.QUEUE_FLUSH, null, null) } catch (_: Exception) {} }) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "TTS", tint = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}

private fun VocabularyEntity.toVocabularyResponse(): VocabularyResponse {
    return VocabularyResponse(
        id = id,
        topicId = topicId,
        word = word,
        meaning = meaning,
        pronunciation = pronunciation,
        exampleSentence = exampleSentence,
        audioUrl = audioUrl,
        difficulty = difficulty
    )
}

// Note: levelCodeColor / levelBgColor helpers are defined in VocabScreen.kt and reused here





