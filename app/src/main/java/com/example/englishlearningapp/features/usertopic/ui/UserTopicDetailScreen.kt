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
    LaunchedEffect(userTopicId) { viewModel.loadTopicVocabs(userTopicId) }

    val topicVocabs by viewModel.topicVocabs.collectAsState()
    val isLoading by viewModel.isLoadingVocabs.collectAsState()
    val error by viewModel.vocabsError.collectAsState()
    val savedVocabIds by viewModel.savedVocabIds.collectAsState()
    val removeSuccess by viewModel.removeSuccess.collectAsState()
    val topic = viewModel.getTopicById(userTopicId)

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
        }
    ) { inner ->
        Box(modifier = Modifier.fillMaxSize().padding(inner).background(Color(0xFF1A1A1A))) {
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
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(topicVocabs, key = { it.id }) { vocab ->
                        VocabExpandableCard(
                            vocab = vocab,
                            savedVocabIds = savedVocabIds,
                            audioPlayer = audioPlayer,
                            showSaveAction = false,
                            onRemoveFromTopic = { pendingRemoveVocab = vocab }
                        )
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








