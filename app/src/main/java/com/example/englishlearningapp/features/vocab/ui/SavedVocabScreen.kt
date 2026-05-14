package com.example.englishlearningapp.features.vocab.ui

import com.example.englishlearningapp.data.remote.NetworkConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.features.usertopic.UserTopicViewModel
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import java.util.Locale

/**
 * SavedVocabScreen displays all saved/bookmarked vocabulary words
 * Users can review saved words and start a quiz
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedVocabScreen(
    navController: NavController,
    viewModel: VocabViewModel = hiltViewModel(),
    userTopicViewModel: UserTopicViewModel = hiltViewModel()
) {
    val savedVocabs by viewModel.savedVocabs.collectAsState()
    val savedVocabIds by userTopicViewModel.savedVocabIds.collectAsState()

    val audioPlayer = rememberVocabAudioPlayer()

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Từ đã lưu")
                        Badge(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ) {
                            Text(savedVocabs.size.toString(), fontSize = 12.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        }
    ) { padding ->
        if (savedVocabs.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🔖", fontSize = 72.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Chưa có từ nào được lưu",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Nhấn biểu tượng dấu trang ở bất kỳ từ nào để lưu lại",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigateUp() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Khám phá chủ đề", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                // Word list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = if (savedVocabs.size >= 4) 96.dp else 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = savedVocabs, key = { it.id }) { vocab ->
                        VocabExpandableCard(
                            vocab = VocabularyResponse(
                                id = vocab.id,
                                topicId = 0,
                                word = vocab.word,
                                meaning = vocab.meaning,
                                pronunciation = vocab.pronunciation,
                                exampleSentence = vocab.exampleSentence,
                                audioUrl = null,
                                exampleAudioUrl = null,
                                difficulty = vocab.difficulty
                            ),
                            savedVocabIds = savedVocabIds,
                            audioPlayer = audioPlayer
                        )
                    }
                }

                // FAB for starting review quiz
                AnimatedVisibility(
                    visible = savedVocabs.size >= 4,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            viewModel.prepareReviewFromSaved()
                            navController.navigate("review")
                        },
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        text = {
                            Text(
                                "🔄 Ôn tập ${savedVocabs.size} từ",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = {
                            Icon(Icons.Default.Quiz, contentDescription = "Review")
                        }
                    )
                }
            }
        }
    }
}





