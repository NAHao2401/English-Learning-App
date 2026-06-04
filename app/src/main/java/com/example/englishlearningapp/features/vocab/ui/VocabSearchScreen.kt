package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.englishlearningapp.data.remote.NetworkConfig
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import com.example.englishlearningapp.data.remote.api.response.UserVocabularyResponse
import com.example.englishlearningapp.navigation.Screen
import com.example.englishlearningapp.features.usertopic.UserTopicViewModel
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModelFactory

private val SearchBg = Color(0xFFF8F6FF)
private val SearchCardBg = Color.White
private val SearchDivider = Color(0xFFE6E2F2)
private val SearchGreen = Color(0xFF4CAF50)
private val SearchTextPrimary = Color(0xFF1D1B2F)
private val SearchTextSecondary = Color(0xFF77738A)
private val SearchTextMuted = Color(0xFF9A97A8)

@Composable
fun VocabSearchScreen(
    navController: NavController,
    vocabVm: VocabViewModel,
    userTopicVm: UserTopicViewModel? = null
) {
    val context = LocalContext.current
    val viewModel = vocabVm ?: composeViewModel(factory = VocabViewModelFactory(context))
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchProgress by viewModel.searchProgress.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val audioPlayer = rememberVocabAudioPlayer()

    var showSaveSheet by remember { mutableStateOf(false) }
    var saveVocab by remember { mutableStateOf<VocabularyResponse?>(null) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSearch()
            audioPlayer.release()
        }
    }

    val backgroundColor = vocabScreenBackground()
    val cardColor = vocabCardContainer()
    val dividerColor = vocabDividerColor()
    val accentColor = vocabAccent()
    val primaryTextColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    val mutedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Surface(
                color = cardColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(94.dp)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.clearSearch()
                            navController.navigateUp()
                        },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = primaryTextColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(primaryTextColor),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        decorationBox = { innerTextField ->
                            Box {
                                if (searchQuery.isBlank()) {
                                    Text(
                                        "Tìm kiếm từ vựng...",
                                        color = secondaryTextColor,
                                        fontSize = 18.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = primaryTextColor
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {}
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(backgroundColor)
        ) {
            when {
                searchQuery.isBlank() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Search,
                            tint = mutedTextColor,
                            modifier = Modifier.size(64.dp),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Tìm kiếm từ vựng",
                            color = secondaryTextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                isSearching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = accentColor)
                    }
                }

                searchResults.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Không tìm thấy từ vựng bạn nhập",
                            color = secondaryTextColor,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "\"$searchQuery\"",
                            color = accentColor,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
                    ) {
                        items(
                            items = searchResults,
                            key = { it.id }
                        ) { vocab ->
                            val mastery = searchProgress[vocab.id]?.masteryLevel ?: 0
                            SearchResultVocabRow(
                                vocab = vocab,
                                mastery = mastery,
                                audioPlayer = audioPlayer,
                                onSaveClick = {
                                    saveVocab = vocab
                                    showSaveSheet = true
                                }
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
        }
    }

    if (showSaveSheet) {
        val vocab = saveVocab
        if (vocab != null) {
            SaveToTopicBottomSheet(
                vocab = vocab,
                onDismiss = {
                    showSaveSheet = false
                    saveVocab = null
                }
            )
        }
    }
}

@Composable
fun SearchResultVocabRow(
    vocab: VocabularyResponse,
    mastery: Int,
    audioPlayer: VocabAudioPlayer,
    onSaveClick: () -> Unit
) {
    var expanded by remember(vocab.id) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "expand"
    )
    val accentColor = vocabAccent()
    val dividerColor = vocabDividerColor()
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    val mutedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            SeedMasteryIcon(masteryLevel = mastery)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = vocab.word,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    IconButton(
                        onClick = {
                            audioPlayer.play(
                                audioUrl = vocab.audioUrl,
                                baseUrl = NetworkConfig.BASE_URL,
                                fallbackText = vocab.word
                            )
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            tint = accentColor,
                            modifier = Modifier.size(17.dp),
                            contentDescription = "Nghe từ"
                        )
                    }
                }

                Text(
                    text = vocab.meaning,
                    color = secondaryTextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onSaveClick) {
                Icon(
                    Icons.Default.BookmarkBorder,
                    tint = mutedTextColor,
                    contentDescription = "Lưu từ",
                    modifier = Modifier.size(20.dp)
                )
            }

            Icon(
                Icons.Default.KeyboardArrowDown,
                tint = mutedTextColor,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(rotation),
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 68.dp, top = 8.dp, end = 8.dp, bottom = 4.dp)
            ) {
                if (!vocab.pronunciation.isNullOrBlank()) {
                    Text(
                        text = vocab.pronunciation,
                        color = mutedTextColor,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (!vocab.exampleSentence.isNullOrBlank()) {
                    HorizontalDivider(
                        color = dividerColor,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${vocab.exampleSentence}\"",
                            color = secondaryTextColor,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                audioPlayer.play(
                                    audioUrl = vocab.exampleAudioUrl,
                                    baseUrl = NetworkConfig.BASE_URL,
                                    fallbackText = vocab.exampleSentence
                                )
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                tint = mutedTextColor,
                                modifier = Modifier.size(16.dp),
                                contentDescription = "Nghe câu ví dụ"
                            )
                        }
                    }
                }

                if (!vocab.difficulty.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = levelCodeColor(vocab.difficulty).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = vocab.difficulty,
                            color = levelCodeColor(vocab.difficulty),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}