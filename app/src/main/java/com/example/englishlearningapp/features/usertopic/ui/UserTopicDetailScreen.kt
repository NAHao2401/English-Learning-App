package com.example.englishlearningapp.features.usertopic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val accentColor = if (isDarkTheme) Color(0xFF4CAF50) else Color(0xFF2F7D62)
    val dividerColor = if (isDarkTheme) Color(0xFFE6E2F2) else Color(0xFFE2E7E4)
    val panelColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color(0xFFFCFBF7)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            if (isDarkTheme) MaterialTheme.colorScheme.background else Color(0xFFF5F2EA),
            panelColor
        )
    )

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
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = accentColor,
                    contentColor = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                expandedHeight = 94.dp,
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = topic?.name ?: "Thư mục",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${topicVocabs.size} từ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE9E7FF))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "back",
                            tint = MaterialTheme.colorScheme.onSurface
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)

            ){}
        }
    ) { inner ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(inner)
            .background(backgroundBrush)) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = accentColor) }
                error != null -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadTopicVocabs(userTopicId) }, colors = ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Thử lại") }
                }
                topicVocabs.isEmpty() -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("📭", fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                    Spacer(Modifier.height(12.dp))
                    Text("Thư mục này chưa có từ nào", color = MaterialTheme.colorScheme.onBackground)
                }
                else -> {
                    val learnedMastery by viewModel.learnedVocabMastery.collectAsState(initial = emptyMap())

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 120.dp
                        )
                    ) {
                        items(items = topicVocabs, key = { it.id }, contentType = { "vocab" }) { vocab ->
                            val mastery = learnedMastery[vocab.id] ?: 0

                            UserTopicVocabRowWithRemove(
                                vocab = vocab,
                                masteryLevel = mastery,
                                audioPlayer = audioPlayer,
                                onRemoveClick = { pendingRemoveVocab = vocab }
                            )

                            HorizontalDivider(
                                color = dividerColor,
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
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val accentColor = if (isDarkTheme) Color(0xFF4CAF50) else Color(0xFF2F7D62)
    val accentSoftColor = if (isDarkTheme) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFEAF4EF)
    val dividerColor = if (isDarkTheme) Color(0xFFE6E2F2) else Color(0xFFE2E7E4)
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isDarkTheme) 0.65f else 0.58f)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            color = if (masteryLevel > 0) accentColor else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        SpeakerIconButton(
                            audioUrl = vocab.audioUrl,
                            baseUrl = NetworkConfig.BASE_URL,
                            fallbackText = vocab.word,
                            audioPlayer = audioPlayer,
                            tint = if (masteryLevel > 0) accentColor else MaterialTheme.colorScheme.onSurface,
                            size = 18.dp
                        )
                    }

                    if (!vocab.pronunciation.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(text = vocab.pronunciation, color = secondaryTextColor, fontSize = 13.sp, fontStyle = FontStyle.Italic)
                    }
                }

                IconButton(onClick = onRemoveClick) {
                    Icon(Icons.Default.Remove, contentDescription = "Remove", tint = Color(0xFFB71C1C))
                }
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = secondaryTextColor, modifier = Modifier.rotate(rotation))
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 74.dp, end = 14.dp, bottom = 14.dp)) {
                    HorizontalDivider(color = dividerColor)
                    Spacer(Modifier.height(8.dp))
                    Text(text = vocab.meaning, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    if (!vocab.exampleSentence.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Row {
                            Text(text = vocab.exampleSentence, color = secondaryTextColor, fontSize = 13.sp, fontStyle = FontStyle.Italic, modifier = Modifier.weight(1f))
                            SpeakerIconButton(
                                audioUrl = vocab.exampleAudioUrl,
                                baseUrl = NetworkConfig.BASE_URL,
                                fallbackText = vocab.exampleSentence,
                                audioPlayer = audioPlayer,
                                tint = secondaryTextColor,
                                size = 16.dp
                            )
                        }
                    }

                    if (masteryLevel > 0) {
                        Spacer(Modifier.height(8.dp))
                        val masteryLabels = mapOf(1 to "Chưa biết", 2 to "Mới học", 3 to "Nhớ tạm", 4 to "Nhớ lâu", 5 to "Thông thạo")
                        Badge(containerColor = accentSoftColor) {
                            Text(masteryLabels[masteryLevel] ?: "", color = accentColor, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}








