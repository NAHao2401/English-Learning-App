package com.example.englishlearningapp.features.vocab.ui

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.englishlearningapp.ui.navigation.Screen
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
    val topicVocabs by viewModel.topicVocabs.collectAsState()
    val difficultyFilter by viewModel.difficultyFilter.collectAsState()
    val savedVocabs by viewModel.savedVocabs.collectAsState()
    val savedIds by remember(savedVocabs) { derivedStateOf { savedVocabs.map { it.id }.toSet() } }
    val topicWithCount: TopicWithCount? = topicsWithCount.find { it.topic.id == topicId }
    val topic = topicWithCount?.topic
    var selectedVocab by remember { mutableStateOf<VocabularyResponse?>(null) }

    // New state from ViewModel (topic progress / study session)
    val topicProgress by viewModel.topicProgress.collectAsState()
    val newWordCount by viewModel.newWordCount.collectAsState()
    val hasMoreWords by viewModel.hasMoreNewWords.collectAsState()
    val learnedCount by viewModel.learnedCount.collectAsState()
    val totalWords by viewModel.totalWords.collectAsState()

    // No new words left in this topic
    val isAllLearned = newWordCount == 0 && totalWords > 0

    val context = LocalContext.current
    val tts = remember { TextToSpeech(context, null) }
    DisposableEffect(Unit) { onDispose { tts.shutdown() } }

    LaunchedEffect(topicId) {
        viewModel.setDifficultyFilter(null)
        viewModel.loadTopicDetail(topicId)
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
                actions = {},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1A1A1A))
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text("$learnedCount/$totalWords đã học", color = Color(0xFF4CAF50), fontSize = 12.sp)
                    if (newWordCount > 0) {
                        Spacer(Modifier.width(12.dp))
                        Text("• $newWordCount từ mới", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { if (!isAllLearned) navController.navigate(Screen.StudyFlashcard.createRoute(topicId)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isAllLearned,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAllLearned) Color(0xFF2A2A2A) else Color(0xFF1565C0),
                        disabledContainerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isAllLearned) {
                        Text("Không có từ nào cần luyện tập", color = Color(0xFF5A5A5A), fontSize = 14.sp)
                    } else {
                        Text("🃏 Học từ mới ($newWordCount)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
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
                    items(items = topicVocabs, key = { it.id }, contentType = { "vocab" }) { vocab: VocabularyResponse ->
                        val mastery = topicProgress[vocab.id]?.masteryLevel ?: 0

                        VocabRowWithSeed(
                            vocab = vocab,
                            masteryLevel = mastery,
                            onSaveClick = { selectedVocab = vocab }
                        )

                        androidx.compose.material3.HorizontalDivider(
                            color = Color(0xFF2A2A2A),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
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
    vocab: VocabularyResponse,
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

@Composable
fun WordItem(
    vocab: VocabularyEntity,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    tts: TextToSpeech
) {
    WordItem(
        vocab = VocabularyResponse(
            id = vocab.id,
            topicId = vocab.topicId,
            word = vocab.word,
            meaning = vocab.meaning,
            pronunciation = vocab.pronunciation,
            exampleSentence = vocab.exampleSentence,
            audioUrl = vocab.audioUrl,
            difficulty = vocab.difficulty
        ),
        isSaved = isSaved,
        onSaveClick = onSaveClick,
        tts = tts
    )
}

// Note: levelCodeColor / levelBgColor helpers are defined in VocabScreen.kt and reused here

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
                    color = if (isFilled) Color(0xFF4CAF50) else Color(0xFF3A3A3A),
                    startAngle = startAngle,
                    sweepAngle = segmentSweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer { rotationZ = -35f }
                .background(
                    color = when (masteryLevel) {
                        0 -> Color(0xFF4A4A4A)
                        1, 2 -> Color(0xFF81C784)
                        3, 4 -> Color(0xFF4CAF50)
                        else -> Color(0xFF2E7D32)
                    },
                    shape = RoundedCornerShape(percent = 50)
                )
        )
    }
}

@Composable
fun VocabRowWithSeed(
    vocab: VocabularyResponse,
    masteryLevel: Int,
    onSaveClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "expand_arrow")

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
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
                SeedMasteryIcon(masteryLevel = masteryLevel)

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vocab.word,
                        color = if (masteryLevel > 0) Color(0xFF4CAF50) else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (!vocab.pronunciation.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(text = vocab.pronunciation, color = Color.Gray, fontSize = 13.sp, fontStyle = FontStyle.Italic)
                    }
                }

                IconButton(onClick = onSaveClick) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = "Lưu từ", tint = Color(0xFF5A5A5A))
                }
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.rotate(rotation))
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 74.dp, end = 14.dp, bottom = 14.dp)) {
                    androidx.compose.material3.HorizontalDivider(color = Color(0xFF3A3A3A))
                    Spacer(Modifier.height(8.dp))
                    Text(text = vocab.meaning, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    if (!vocab.exampleSentence.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(text = vocab.exampleSentence, color = Color.LightGray, fontSize = 13.sp, fontStyle = FontStyle.Italic)
                    }

                    if (masteryLevel > 0) {
                        Spacer(Modifier.height(8.dp))
                        val masteryLabels = mapOf(1 to "Chưa biết", 2 to "Mới học", 3 to "Nhớ tạm", 4 to "Nhớ lâu", 5 to "Thông thạo")
                        Badge(containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)) {
                            Text(masteryLabels[masteryLevel] ?: "", color = Color(0xFF4CAF50), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}





