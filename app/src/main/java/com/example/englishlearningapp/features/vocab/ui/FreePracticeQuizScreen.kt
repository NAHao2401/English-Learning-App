package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel

@Composable
fun FreePracticeQuizScreen(
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

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có từ nào cần ôn tập!", color = Color.White)
        }
        return
    }

    if (showResult) {
        QuizResultScreen(
            correctCount = correctCount,
            totalCount = questions.size,
            onFinish = {
                vocabViewModel.loadFreePracticeWords()
                navController.navigateUp()
            }
        )
        return
    }

    val question = questions.getOrNull(currentIndex)
    val isLastQuestion = currentIndex == questions.size - 1
    val isCorrect = selectedIndex == question?.correctIndex
    val masteryMap = learnedData?.items?.associate { it.vocabularyId to it.masteryLevel }.orEmpty()

    fun handleAnswer(index: Int) {
        if (isAnswered) return
        selectedIndex = index
        isAnswered = true
        if (index == question?.correctIndex) correctCount++
    }

    fun goNext() {
        if (isLastQuestion) {
            showResult = true
        } else {
            currentIndex++
            selectedIndex = null
            isAnswered = false
        }
    }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.IconButton(onClick = { navController.navigateUp() }) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 16.dp, bottom = 8.dp, top = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(16.dp))
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1f) / questions.size },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFF3A3A3A)
                    )
                    Text(
                        "${currentIndex + 1}/${questions.size}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Chọn nghĩa",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.weight(1f))
                val mastery = question?.vocabId?.let { masteryMap[it] } ?: 0
                LearnedSeedIcon(masteryLevel = mastery)
            }

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Từ sau đây mang nghĩa là gì?",
                        color = Color(0xFFAAAAAA),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = question?.word ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center
                    )
                    if (!question?.pronunciation.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = question?.pronunciation ?: "",
                            color = Color(0xFF9E9E9E),
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            val optionLabels = listOf("A", "B", "C", "D")
            question?.options?.forEachIndexed { index, option ->
                val isSelected = selectedIndex == index
                val isThisCorrect = index == question.correctIndex

                val (bgColor, borderColor, textColor) = when {
                    !isAnswered -> Triple(Color(0xFF2A2A2A), Color(0xFF4A4A4A), Color.White)
                    isThisCorrect -> Triple(Color(0xFF1B5E20), Color(0xFF4CAF50), Color(0xFF4CAF50))
                    isSelected && !isThisCorrect -> Triple(Color(0xFF7F0000), Color(0xFFF44336), Color(0xFFF44336))
                    else -> Triple(Color(0xFF2A2A2A), Color(0xFF3A3A3A), Color(0xFF7A7A7A))
                }

                Card(
                    onClick = { handleAnswer(index) },
                    enabled = !isAnswered,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    border = BorderStroke(1.5.dp, borderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(30.dp).background(borderColor.copy(alpha = 0.2f), CircleShape).clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(optionLabels[index], color = borderColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = option,
                            color = textColor,
                            fontSize = 15.sp,
                            fontWeight = if (isThisCorrect && isAnswered) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )

                        if (isAnswered) {
                            when {
                                isThisCorrect -> Icon(Icons.Default.CheckCircle, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp), contentDescription = null)
                                isSelected -> Icon(Icons.Default.Cancel, tint = Color(0xFFF44336), modifier = Modifier.size(20.dp), contentDescription = null)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(visible = isAnswered, enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })) {
                Button(
                    onClick = { goNext() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isLastQuestion) "Hoàn thành bài học ✓" else "Câu hỏi tiếp theo →",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            AnimatedVisibility(visible = isAnswered) {
                Text(
                    text = if (isCorrect) "✓ Chính xác!" else "✗ Chưa đúng.",
                    color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
    }
}
