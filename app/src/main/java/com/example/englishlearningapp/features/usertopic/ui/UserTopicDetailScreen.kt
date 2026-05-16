package com.example.englishlearningapp.features.usertopic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.features.usertopic.UserTopicViewModel
import com.example.englishlearningapp.features.usertopic.UserTopicViewModelFactory
import com.example.englishlearningapp.features.vocab.ui.VocabExpandableCard
import com.example.englishlearningapp.features.vocab.ui.rememberVocabAudioPlayer
import com.example.englishlearningapp.features.vocab.ui.VocabRowWithSeed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.KeyboardArrowDown
import com.example.englishlearningapp.features.vocab.ui.SeedMasteryIcon
import com.example.englishlearningapp.features.vocab.ui.SpeakerIconButton
import com.example.englishlearningapp.data.remote.NetworkConfig
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTopicDetailScreen(
    navController: NavController,
    userTopicVm: UserTopicViewModel? = null,
    userTopicId: Int
) {
    val context = LocalContext.current
    val viewModel = userTopicVm ?: composeViewModel(factory = UserTopicViewModelFactory(context))
    LaunchedEffect(userTopicId) {
        // ensure we have the user's topics loaded so getTopicById() can return the correct name
        viewModel.loadUserTopics()
        viewModel.loadTopicVocabs(userTopicId)
        viewModel.refreshLearnedVocabMastery()
    }

    val topicVocabs by viewModel.topicVocabs.collectAsState()
    val isLoading by viewModel.isLoadingVocabs.collectAsState()
    val error by viewModel.vocabsError.collectAsState()
    val savedVocabIds by viewModel.savedVocabIds.collectAsState()
    val removeSuccess by viewModel.removeSuccess.collectAsState()
    val userTopics by viewModel.userTopics.collectAsState()
    val topic = userTopics.find { it.id == userTopicId }

    var pendingRemoveVocab by remember { mutableStateOf<com.example.englishlearningapp.data.remote.api.response.VocabularyResponse?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(removeSuccess) {
        if (!removeSuccess.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = removeSuccess!!,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
            viewModel.clearRemoveFeedback()
        }
    }

    val audioPlayer = rememberVocabAudioPlayer()

    Scaffold(
        containerColor = Color(0xFF4CAF50),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                expandedHeight = 94.dp,
                windowInsets = WindowInsets(0),
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = topic?.name ?: "Thư mục",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${topicVocabs.size} từ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4CAF50))

            ){}
        }
    ) { inner ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(inner)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFF1A1A1A))) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF4CAF50)) }
                error != null -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(error ?: "", color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadTopicVocabs(userTopicId) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("Thử lại") }
                }
                topicVocabs.isEmpty() -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("📭", fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                    Spacer(Modifier.height(12.dp))
                    Text("Thư mục này chưa có từ nào", color = Color.White)
                }
                else -> {
                    val learnedMastery by viewModel.learnedVocabMastery.collectAsState(initial = emptyMap())

                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                        items(items = topicVocabs, key = { it.id }, contentType = { "vocab" }) { vocab ->
                            val mastery = learnedMastery[vocab.id] ?: 0

                            UserTopicVocabRowWithRemove(
                                vocab = vocab,
                                masteryLevel = mastery,
                                audioPlayer = audioPlayer,
                                onRemoveClick = { pendingRemoveVocab = vocab }
                            )

                            HorizontalDivider(
                                color = Color(0xFF2A2A2A),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            if (pendingRemoveVocab != null) {
                AlertDialog(
                    onDismissRequest = { pendingRemoveVocab = null },
                    title = { Text("Xóa từ khỏi thư mục") },
                    text = { Text("Bạn có chắc muốn xóa từ này khỏi thư mục?") },
                    confirmButton = {
                        TextButton(onClick = {
                            val vocabToRemove = pendingRemoveVocab
                            pendingRemoveVocab = null
                            if (vocabToRemove != null) {
                                viewModel.removeVocabularyFromTopic(userTopicId, vocabToRemove.id)
                            }
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { pendingRemoveVocab = null }) { Text("Không") }
                    }
                )
            }
        }
    }
}

@Composable
private fun UserTopicVocabRowWithRemove(
    vocab: com.example.englishlearningapp.data.remote.api.response.VocabularyResponse,
    masteryLevel: Int,
    audioPlayer: com.example.englishlearningapp.features.vocab.ui.VocabAudioPlayer,
    onRemoveClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, animationSpec = tween(durationMillis = 300))

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
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
                    Row {
                        Text(
                            text = vocab.word,
                            color = if (masteryLevel > 0) Color(0xFF4CAF50) else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        SpeakerIconButton(
                            audioUrl = vocab.audioUrl,
                            baseUrl = NetworkConfig.BASE_URL,
                            fallbackText = vocab.word,
                            audioPlayer = audioPlayer,
                            tint = if (masteryLevel > 0) Color(0xFF4CAF50) else Color.White,
                            size = 18.dp
                        )
                    }

                    if (!vocab.pronunciation.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(text = vocab.pronunciation, color = Color.Gray, fontSize = 13.sp, fontStyle = FontStyle.Italic)
                    }
                }

                IconButton(onClick = onRemoveClick) {
                    Icon(Icons.Default.Remove, contentDescription = "Remove", tint = Color(0xFFB71C1C))
                }
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.rotate(rotation))
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 74.dp, end = 14.dp, bottom = 14.dp)) {
                    HorizontalDivider(color = Color(0xFF3A3A3A))
                    Spacer(Modifier.height(8.dp))
                    Text(text = vocab.meaning, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    if (!vocab.exampleSentence.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Row {
                            Text(text = vocab.exampleSentence, color = Color.LightGray, fontSize = 13.sp, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f))
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








