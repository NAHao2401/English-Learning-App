package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
// weight extension is available from layout package; avoid explicit import to prevent internal access issues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    navController: NavController,
    topicId: Int,
    vocabVm: VocabViewModel? = null
) {
    val context = LocalContext.current
    val viewModel = vocabVm ?: composeViewModel(factory = com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModelFactory(context))
    val vocabs by viewModel.currentTopicVocabs.collectAsState()

    LaunchedEffect(topicId) {
        if (vocabs.isEmpty()) viewModel.selectTopic(topicId)
    }

    if (vocabs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        }
        return
    }

    var currentIndex by remember(vocabs) { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val vocab = vocabs[currentIndex]

    ScaffoldFlashcard(
        navController = navController,
        title = "${currentIndex + 1} / ${vocabs.size}",
        onClose = { navController.navigateUp() },
        content = {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                LinearProgressIndicator(progress = (currentIndex + 1f) / vocabs.size, color = Color(0xFF4CAF50), trackColor = Color(0xFF3A3A3A), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.size(20.dp))

                val rotation by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f, animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer { rotationY = rotation }
                    .clickable { isFlipped = !isFlipped }, contentAlignment = Alignment.Center) {
                    if (rotation <= 90f) {
                        Card(modifier = Modifier.fillMaxSize(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), shape = RoundedCornerShape(20.dp)) {
                            Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text("🇬🇧", fontSize = 32.sp)
                                Spacer(modifier = Modifier.size(16.dp))
                                Text(vocab.word, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 34.sp, textAlign = TextAlign.Center)
                                if (!vocab.pronunciation.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text(vocab.pronunciation, color = Color.Gray, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.size(24.dp))
                                Text("Nhấn để xem nghĩa", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Card(modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = -1f }, colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3A2D)), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), shape = RoundedCornerShape(20.dp)) {
                            Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text("🇻🇳", fontSize = 32.sp)
                                Spacer(modifier = Modifier.size(16.dp))
                                Text(vocab.meaning, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp, textAlign = TextAlign.Center)
                                if (!vocab.exampleSentence.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Text(vocab.exampleSentence, color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = { if (currentIndex > 0) { currentIndex--; isFlipped = false } }, enabled = currentIndex > 0, border = BorderStroke(1.dp, if (currentIndex > 0) Color(0xFF4CAF50) else Color.Gray), modifier = Modifier.weight(1f)) { Text("← Trước", color = if (currentIndex > 0) Color(0xFF4CAF50) else Color.Gray) }

                    Spacer(modifier = Modifier.weight(0.02f))

                    Button(onClick = { isFlipped = !isFlipped }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), modifier = Modifier.weight(1f)) { Text("🔄 Lật") }

                    Spacer(modifier = Modifier.weight(0.02f))

                    if (currentIndex < vocabs.size - 1) {
                        Button(onClick = { currentIndex++; isFlipped = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A)), modifier = Modifier.weight(1f), border = BorderStroke(1.dp, Color(0xFF4CAF50))) { Text("Tiếp →", color = Color.White) }
                    } else {
                        Button(onClick = { navController.navigateUp() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), modifier = Modifier.weight(1f)) { Text("✅ Xong") }
                    }
                }

                Spacer(modifier = Modifier.size(8.dp))

                if (!vocab.difficulty.isNullOrBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Badge(containerColor = levelCodeColor(vocab.difficulty).copy(alpha = 0.2f)) { Text(vocab.difficulty, color = levelCodeColor(vocab.difficulty), fontSize = 11.sp) }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScaffoldFlashcard(navController: NavController, title: String, onClose: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(title) }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) { androidx.compose.material3.Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
        }, actions = {
            IconButton(onClick = onClose) { androidx.compose.material3.Icon(Icons.Default.Close, contentDescription = "Close") }
        }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1A1A1A)))
    }) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}






