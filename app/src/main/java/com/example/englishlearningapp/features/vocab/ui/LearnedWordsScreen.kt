package com.example.englishlearningapp.features.vocab.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.data.remote.api.response.LearnedVocabItem
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.features.vocab.ui.SaveToTopicBottomSheet
import com.example.englishlearningapp.data.remote.NetworkConfig

private val DarkBg = Color(0xFF1A1A1A)
private val CardBg = Color(0xFF2A2A2A)
private val PrimaryGreen = Color(0xFF4CAF50)
private val OrangeAccent = Color(0xFFFF8C00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnedWordsScreen(
    navController: NavController,
    vocabVm: VocabViewModel? = null
) {
    val context = LocalContext.current
    val viewModel = vocabVm ?: composeViewModel(factory = com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModelFactory(context))
    val learnedData by viewModel.learnedVocabs.collectAsState()
    val isLoading by viewModel.isLoadingLearned.collectAsState()

    val audioPlayer = rememberVocabAudioPlayer()

    LaunchedEffect(Unit) {
        viewModel.loadLearnedVocabs()
    }

    val items = learnedData?.items ?: emptyList()
    val total = learnedData?.total ?: 0
    val dueCount = learnedData?.dueCount ?: 0

    var showSaveSheet by remember { mutableStateOf(false) }
    var selectedVocabId by remember { mutableStateOf<Int?>(null) }
    var selectedVocabWord by remember { mutableStateOf("") }
    var showFreePracticeSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = "Các từ đã học",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF232323),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (dueCount > 0) {
                        Button(
                            onClick = { navController.navigate("review_quiz") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangeAccent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.WaterDrop,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Ôn tập ngay ($dueCount từ)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Không có từ nào cần luyện tập",
                                color = Color(0xFF7A7A7A),
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    viewModel.loadFreePracticeWords()
                                    showFreePracticeSheet = true
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    tint = Color(0xFF7A7A7A),
                                    contentDescription = "Tự luyện tập"
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }

            items.isEmpty() && !isLoading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌱", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Bạn chưa học từ nào",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Bắt đầu học từ vựng để xem chúng ở đây!",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)) {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = padding.calculateBottomPadding() + 8.dp,
                            start = 0.dp,
                            end = 0.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Tổng: $total từ",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (dueCount > 0) {
                                    Spacer(Modifier.width(8.dp))
                                    Surface(
                                        color = OrangeAccent,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            " $dueCount cần ôn ",
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }

                        items(
                            items = items,
                            key = { it.vocabularyId }
                        ) { item ->
                            val vocabResp = VocabularyResponse(
                                id = item.vocabularyId,
                                topicId = 0,
                                word = item.word,
                                meaning = item.meaning,
                                pronunciation = item.pronunciation,
                                exampleSentence = item.exampleSentence,
                                audioUrl = item.audioUrl,
                                exampleAudioUrl = item.exampleAudioUrl,
                                difficulty = null
                            )

                            VocabRowWithSeed(
                                vocab = vocabResp,
                                masteryLevel = item.masteryLevel,
                                audioPlayer = audioPlayer,
                                onSaveClick = {
                                    selectedVocabId = item.vocabularyId
                                    selectedVocabWord = item.word
                                    showSaveSheet = true
                                },
                                useLeafIcon = true
                            )

                            androidx.compose.material3.HorizontalDivider(
                                color = Color(0xFF2A2A2A),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSaveSheet && selectedVocabId != null) {
        SaveToTopicBottomSheetWithId(
            vocabId = selectedVocabId!!,
            vocabWord = selectedVocabWord,
            onDismiss = { showSaveSheet = false }
        )
    }

    if (showFreePracticeSheet) {
        LearnedWordsFreePracticeModeBottomSheet(
            onDismiss = { showFreePracticeSheet = false },
            onSelectMode = { route ->
                showFreePracticeSheet = false
                navController.navigate(route)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnedWordsFreePracticeModeBottomSheet(
    onDismiss: () -> Unit,
    onSelectMode: (route: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF2A2A2A),
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFF4A4A4A), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                "Chọn cách luyện tập",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HorizontalDivider(color = Color(0xFF3A3A3A))
            Spacer(Modifier.height(16.dp))

            val modes = listOf(
                Triple(
                    "free_practice_normal",
                    "Luyện tập thông thường",
                    "Trắc nghiệm chọn 1 / 4 đáp án"
                ),
                Triple(
                    "free_practice_listening",
                    "Luyện tập nghe",
                    "Nghe và chọn nghĩa đúng"
                ),
                Triple(
                    "free_practice_challenge",
                    "Luyện tập thử thách",
                    "Nhập từ tiếng Anh theo nghĩa"
                )
            )

            modes.forEach { (route, title, subtitle) ->
                Button(
                    onClick = { onSelectMode(route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(bottom = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                subtitle,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LearnedSeedIcon(
    masteryLevel: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(52.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.5.dp.toPx()
            val segmentSweep = 60f
            val gapSweep = 12f

            repeat(5) { i ->
                val startAngle = -90f + i * (segmentSweep + gapSweep)
                val filled = i < masteryLevel

                drawArc(
                    color = if (filled) Color(0xFF4CAF50) else Color(0xFF2A2A2A),
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
fun LearnedWordRow(
    item: LearnedVocabItem,
    audioPlayer: VocabAudioPlayer,
    onSaveClick: (vocabularyId: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "expand"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LearnedSeedIcon(masteryLevel = item.masteryLevel)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.word,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.width(4.dp))
                    SpeakerIconButton(
                        audioUrl = item.audioUrl,
                        baseUrl = NetworkConfig.BASE_URL,
                        fallbackText = item.word,
                        audioPlayer = audioPlayer,
                        tint = PrimaryGreen,
                        size = 18.dp
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.meaning,
                    color = Color(0xFFCCCCCC),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { onSaveClick(item.vocabularyId) }) {
                Icon(
                    Icons.Default.BookmarkBorder,
                    tint = Color(0xFF5A5A5A),
                    contentDescription = "Lưu từ"
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 80.dp,
                        end = 16.dp,
                        bottom = 14.dp
                    )
            ) {
                HorizontalDivider(
                    color = Color(0xFF2A2A2A),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Pronunciation
                if (!item.pronunciation.isNullOrBlank()) {
                    Text(
                        text = item.pronunciation,
                        color = Color(0xFF9E9E9E),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(Modifier.height(6.dp))
                }

                // Vietnamese meaning (bold, prominent)
                Text(
                    text = item.meaning,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Example sentence
                if (!item.exampleSentence.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "\"${item.exampleSentence}\"",
                            color = Color(0xFF7A7A7A),
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.weight(1f)
                        )
                        SpeakerIconButton(
                            audioUrl = item.exampleAudioUrl,
                            baseUrl = NetworkConfig.BASE_URL,
                            fallbackText = item.exampleSentence,
                            audioPlayer = audioPlayer,
                            tint = Color(0xFF5A5A5A),
                            size = 16.dp
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Mastery badge + due badge row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (masteryLabel, masteryColor) = when (item.masteryLevel) {
                        1 -> "Chưa biết" to Color(0xFF9E9E9E)
                        2 -> "Mới học" to Color(0xFF81C784)
                        3 -> "Nhớ tạm" to Color(0xFF4CAF50)
                        4 -> "Nhớ lâu" to Color(0xFF2E7D32)
                        5 -> "Thông thạo" to Color(0xFF1B5E20)
                        else -> "Chưa học" to Color.Gray
                    }
                    Surface(
                        color = masteryColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = masteryLabel,
                            color = masteryColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (item.isDue) {
                        Surface(
                            color = Color(0xFFFF8C00).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Đến hạn ôn",
                                color = Color(0xFFFF8C00),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    "Đã ôn ${item.reviewCount} lần",
                    color = Color(0xFF5A5A5A),
                    fontSize = 12.sp
                )
            }
        }

        HorizontalDivider(
            color = Color(0xFF2A2A2A),
            thickness = 0.5.dp
        )
    }
}
