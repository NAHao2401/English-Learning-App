package com.example.englishlearningapp.features.vocab.ui

import com.example.englishlearningapp.data.remote.NetworkConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel

@Composable
fun FreePracticeListeningScreen(
    navController: NavController,
    vocabViewModel: VocabViewModel
) {
    val allWords by vocabViewModel.freePracticeWords.collectAsState()
    val distractorPool by vocabViewModel.freePracticeDistractorPool.collectAsState()
    val learnedData by vocabViewModel.learnedVocabs.collectAsState()

    LaunchedEffect(Unit) {
        vocabViewModel.loadFreePracticeWords()
        vocabViewModel.loadLearnedVocabs()
    }

    val questions = remember(allWords, distractorPool) {
        if (allWords.isEmpty()) emptyList()
        else {
            val combined = (allWords + distractorPool).distinctBy { it.id }
            buildQuizQuestionsFromVocabularyResponses(allWords, combined)
        }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    val audioPlayer = rememberVocabAudioPlayer()

    val question = questions.getOrNull(currentIndex)
    val masteryMap = learnedData?.items?.associate { it.vocabularyId to it.masteryLevel }.orEmpty()
    val isLastQuestion = currentIndex == questions.size - 1

    LaunchedEffect(currentIndex, questions.size) {
        question?.let { q ->
            val audioUrl = allWords.find { it.id == q.vocabId }?.audioUrl
            audioPlayer.play(audioUrl, NetworkConfig.BASE_URL, q.word)
        }
    }

    if (showResult) {
        QuizResultScreen(correctCount = correctCount, totalCount = questions.size, onFinish = {
            vocabViewModel.loadFreePracticeWords()
            navController.navigateUp()
        })
        return
    }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có từ nào cần ôn tập!", color = Color.White)
        }
        return
    }

    Scaffold(containerColor = Color(0xFF1A1A1A), topBar = {
        Column(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 16.dp, bottom = 8.dp, top = 0.dp), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(16.dp))
                LinearProgressIndicator(progress = { (currentIndex + 1f) / questions.size }, modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)), color = Color(0xFF4CAF50), trackColor = Color(0xFF3A3A3A))
                Text("${currentIndex + 1}/${questions.size}", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
                Spacer(Modifier.width(12.dp))
            }
        }
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Chọn nghĩa", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.weight(1f))
                val mastery = question?.vocabId?.let { masteryMap[it] } ?: 0
                LearnedSeedIcon(masteryLevel = mastery)
            }

            Spacer(Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(100.dp).background(Color(0xFF1565C0), CircleShape).clickable {
                    val audioUrl = allWords.find { it.id == question?.vocabId }?.audioUrl
                    question?.let { q -> audioPlayer.play(audioUrl, NetworkConfig.BASE_URL, q.word) }
                }, contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, tint = Color.White, modifier = Modifier.size(48.dp), contentDescription = "Nghe từ")
                }
            }

            Spacer(Modifier.height(40.dp))

            val optionLabels = listOf("A", "B", "C", "D")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(0, 1).forEach { index ->
                        AnswerOptionCard(option = question?.options?.getOrNull(index) ?: "", label = optionLabels[index], isAnswered = isAnswered, isSelected = selectedIndex == index, isCorrect = index == question?.correctIndex, modifier = Modifier.weight(1f).height(130.dp), onClick = {
                            if (!isAnswered) {
                                selectedIndex = index
                                isAnswered = true
                                if (index == question?.correctIndex) correctCount++
                            }
                        })
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(2, 3).forEach { index ->
                        AnswerOptionCard(option = question?.options?.getOrNull(index) ?: "", label = optionLabels[index], isAnswered = isAnswered, isSelected = selectedIndex == index, isCorrect = index == question?.correctIndex, modifier = Modifier.weight(1f).height(130.dp), onClick = {
                            if (!isAnswered) {
                                selectedIndex = index
                                isAnswered = true
                                if (index == question?.correctIndex) correctCount++
                            }
                        })
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(visible = isAnswered, enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })) {
                Button(onClick = {
                    if (isLastQuestion) showResult = true else {
                        currentIndex++
                        selectedIndex = null
                        isAnswered = false
                    }
                }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)), shape = RoundedCornerShape(12.dp)) {
                    Text(if (isLastQuestion) "Hoàn thành bài học ✓" else "Câu hỏi tiếp theo →", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
