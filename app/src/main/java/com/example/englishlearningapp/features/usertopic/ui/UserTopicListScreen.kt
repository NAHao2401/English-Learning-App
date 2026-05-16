package com.example.englishlearningapp.features.usertopic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.englishlearningapp.features.usertopic.UserTopicViewModel
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.data.remote.api.response.UserTopicResponse
import com.example.englishlearningapp.features.usertopic.UserTopicViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTopicListScreen(
    navController: NavController,
    userTopicVm: UserTopicViewModel? = null,
    vocabVm: VocabViewModel? = null
) {
    val context = LocalContext.current
    val viewModel = userTopicVm ?: composeViewModel(factory = UserTopicViewModelFactory(context))
    val vocabViewModel = vocabVm ?: composeViewModel(factory = com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModelFactory(context))

    LaunchedEffect(Unit) { viewModel.loadUserTopics() }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadUserTopics()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val userTopics by viewModel.userTopics.collectAsState()
    val isLoading by viewModel.isLoadingTopics.collectAsState()
    val error by viewModel.topicsError.collectAsState()
    val showDialog by viewModel.showCreateDialog.collectAsState()
    val createError by viewModel.createError.collectAsState()
    val currentUser by vocabViewModel.currentUser.collectAsState()
    val topicWordCounts by viewModel.topicWordCounts.collectAsState()
    val topicLearnedCounts by viewModel.topicLearnedCounts.collectAsState()
    val displayTopicWordCounts = if (topicWordCounts.isNotEmpty()) topicWordCounts else userTopics.associate { it.id to it.wordCount }
    val displayTopicLearnedCounts = if (topicLearnedCounts.isNotEmpty()) topicLearnedCounts else userTopics.associate { it.id to it.learnedCount }

    LaunchedEffect(userTopics) {
        userTopics.forEach { topic ->
            if ((displayTopicWordCounts[topic.id] ?: topic.wordCount) <= 0) {
                viewModel.refreshTopicWordCount(topic.id)
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFF4CAF50),
        topBar = {
            TopAppBar(
                expandedHeight = 94.dp,
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back", tint = Color.White)
                    }
                },
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .border(2.5.dp, Color.White, androidx.compose.foundation.shape.CircleShape)
                                    .padding(2.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(Color(0xFF1B3A2D)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!currentUser?.avatarUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = currentUser!!.avatarUrl,
                                        contentDescription = "avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Text(
                                        text = currentUser?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = "Thư mục của tôi",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                val totalWords = userTopics.sumOf { topic -> displayTopicWordCounts[topic.id] ?: topic.wordCount }
                                val totalLearned = userTopics.sumOf { topic -> displayTopicLearnedCounts[topic.id] ?: topic.learnedCount }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFF388E3C),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "$totalLearned/$totalWords đã học",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    
                                }
                            }
                        }
                    }
                },
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
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
                error != null -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("⚠️", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                    Text(error ?: "", color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadUserTopics() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("Thử lại") }
                }
                userTopics.isEmpty() -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("📂", fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                    Spacer(Modifier.height(12.dp))
                    Text("Chưa có thư mục nào", color = Color.White)
                }
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1A1A1A)),
                    contentPadding = PaddingValues(top = 28.dp, bottom = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    items(userTopics, key = { it.id }) { topic ->
                        UserTopicCircleItem(
                            topic = topic,
                            wordCount = displayTopicWordCounts[topic.id] ?: topic.wordCount,
                            learnedCount = displayTopicLearnedCounts[topic.id] ?: topic.learnedCount,
                            onClick = { navController.navigate("user_topic_detail/${topic.id}") }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xFF232323))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Không có từ nào cần luyện tập",
                        color = Color(0xFFB0B0B0),
                        fontSize = 14.sp
                    )
                        var showPracticeSheet by remember { mutableStateOf(false) }
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.clickable {
                                viewModel.loadSelfPracticeWords()
                                showPracticeSheet = true
                            }
                        )

                        if (showPracticeSheet) {
                            SelfPracticeModeBottomSheet(
                                onDismiss = { showPracticeSheet = false },
                                onSelectMode = { route ->
                                    showPracticeSheet = false
                                    navController.navigate(route)
                                }
                            )
                        }
                }
            }

            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 88.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tạo thư mục mới",
                    modifier = Modifier.size(28.dp)
                )
            }
                    if (showDialog) {
                        var newName by remember(showDialog) { mutableStateOf("") }
                        val duplicate = userTopics.any { it.name.equals(newName.trim(), ignoreCase = true) }
                        val nameError = when {
                            newName.isBlank() -> "Tên thư mục không được để trống"
                            duplicate -> "Tên thư mục đã tồn tại"
                            else -> null
                        }

                        AlertDialog(
                            onDismissRequest = { viewModel.hideCreateDialog() },
                            title = { Text("Tạo thư mục mới") },
                            text = {
                                Column {
                                    OutlinedTextField(
                                        value = newName,
                                        onValueChange = { newName = it },
                                        label = { Text("Tên thư mục") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    if (nameError != null) {
                                        Spacer(Modifier.height(6.dp))
                                        Text(nameError, color = Color(0xFFFF5252), fontSize = 12.sp)
                                    } else if (!createError.isNullOrBlank()) {
                                        Spacer(Modifier.height(6.dp))
                                        Text(createError ?: "", color = Color(0xFFFF5252), fontSize = 12.sp)
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.createUserTopic(newName.trim(), null)
                                }, enabled = nameError == null) { Text("Tạo") }
                            },
                            dismissButton = {
                                TextButton(onClick = { viewModel.hideCreateDialog() }) { Text("Hủy") }
                            }
                        )
                    }
        }
    }
}

@Composable
fun UserTopicCircleItem(
    topic: UserTopicResponse,
    wordCount: Int,
    learnedCount: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(180.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .border(
                    width = 3.5.dp,
                    color = Color(0xFF4CAF50),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .padding(5.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = Color(0xFF5A5A5A),
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = topic.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "$learnedCount/$wordCount",
                    color = Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
        }
    }
}



