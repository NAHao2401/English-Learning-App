package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewQuizScreen(
    navController: NavController,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val learnedData by viewModel.learnedVocabs.collectAsState()

    val allItems = learnedData?.items ?: emptyList()
    // Ensure data is loaded (in case user navigated directly)
    LaunchedEffect(Unit) { viewModel.loadLearnedVocabs() }

    // Use only the isDue flag from backend; backend must set it correctly
    val dueItems = allItems.filter { it.isDue }

    val questions = remember(dueItems) {
        if (dueItems.isEmpty()) emptyList()
        else buildQuizQuestions(dueItems, allItems)
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

    val question = questions.getOrNull(currentIndex)
    val isLastQuestion = currentIndex == questions.size - 1
    val isCorrect = selectedIndex == question?.correctIndex

    fun handleAnswer(index: Int) {
        if (isAnswered) return
        selectedIndex = index
        isAnswered = true
        if (index == question?.correctIndex) correctCount++
    }

    fun goNext() {
        question?.let { q ->
            val wasCorrect = selectedIndex == q.correctIndex
            viewModel.rateQuizAnswer(
                vocabularyId = q.vocabId,
                currentMastery = q.masteryLevel,
                isCorrect = wasCorrect
            )
        }
        if (isLastQuestion) {
            showResult = true
        } else {
            currentIndex++
            selectedIndex = null
            isAnswered = false
        }
    }

    if (showResult) {
        QuizResultScreen(
            correctCount = correctCount,
            totalCount = questions.size,
            onFinish = {
                viewModel.loadLearnedVocabs()
                navController.navigateUp()
            }
        )
        return
    }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp,
                            end = 16.dp,
                            bottom = 8.dp, // Chỉ padding phía dưới
                            top = 0.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Chọn nghĩa",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.weight(1f))
                val mastery = allItems.find { it.vocabularyId == question?.vocabId }?.masteryLevel ?: 0
                LearnedSeedIcon(masteryLevel = mastery)
            }

            Spacer(Modifier.height(32.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(6.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Từ sau đây mang nghĩa là gì?", color = Color(0xFFAAAAAA), fontSize = 14.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text(text = question?.word ?: "", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, textAlign = TextAlign.Center)
                    if (!question?.pronunciation.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(text = question?.pronunciation ?: "", color = Color(0xFF9E9E9E), fontSize = 15.sp, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
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

                Card(onClick = { handleAnswer(index) }, enabled = !isAnswered, modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), colors = CardDefaults.cardColors(containerColor = bgColor), border = BorderStroke(1.5.dp, borderColor), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(30.dp).background(borderColor.copy(alpha = 0.2f), CircleShape).clip(CircleShape), contentAlignment = Alignment.Center) {
                            Text(optionLabels[index], color = borderColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(text = option, color = textColor, fontSize = 15.sp, fontWeight = if (isThisCorrect && isAnswered) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f))

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
                Button(onClick = { goNext() }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), shape = RoundedCornerShape(12.dp)) {
                    Text(text = if (isLastQuestion) "Hoàn thành bài học ✓" else "Câu hỏi tiếp theo →", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            AnimatedVisibility(visible = isAnswered) {
                Text(text = if (isCorrect) "✓ Chính xác! +1 độ thông thạo" else "✗ Chưa đúng. -1 độ thông thạo", color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
        }
    }
}
