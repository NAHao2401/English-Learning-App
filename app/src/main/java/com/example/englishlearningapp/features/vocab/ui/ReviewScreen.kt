package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel

/**
 * Quiz question data class for multiple choice review
 */
data class QuizQuestion(
    val vocab: VocabularyEntity,
    val options: List<String>,      // 4 answer options (meanings)
    val correctIndex: Int            // index of correct answer
)

/**
 * ReviewScreen - Multiple choice quiz for vocabulary review
 * Users select the correct meaning for each word
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    navController: NavController,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val reviewVocabs by viewModel.reviewVocabs.collectAsState()

    // Ensure vocab pool is loaded for generating wrong options
    LaunchedEffect(Unit) {
        viewModel.ensureVocabPool()
    }

    // Guard: show loading while preparing
    if (reviewVocabs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
                Text(
                    "Đang chuẩn bị câu hỏi...",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    // Generate questions
    fun generateQuestions(vocabs: List<VocabularyEntity>): List<QuizQuestion> {
        return vocabs.take(minOf(10, vocabs.size)).map { vocab ->
            val wrongOptions = viewModel.getWrongOptions(vocab.id, 3)
            val allOptions = (wrongOptions + vocab.meaning).shuffled()
            QuizQuestion(
                vocab = vocab,
                options = allOptions,
                correctIndex = allOptions.indexOf(vocab.meaning)
            )
        }
    }

    var questions by remember(reviewVocabs) {
        mutableStateOf(generateQuestions(reviewVocabs))
    }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }

    val isAnswered = selectedIndex != null
    val question by remember(questions, currentIndex) {
        derivedStateOf { questions.getOrNull(currentIndex) }
    }

    if (question == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có câu hỏi", color = Color.Gray)
        }
        return
    }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ôn tập từ vựng") },
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
        if (!showResult) {
            // Quiz body
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    // Progress bar
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1f) / questions.size },
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFF3A3A3A),
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Câu ${currentIndex + 1} / ${questions.size}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                item {
                    // Word card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3A2D)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                question!!.vocab.word,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                            val pronunciationText = question!!.vocab.pronunciation
                            if (!pronunciationText.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    pronunciationText,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Chọn nghĩa đúng",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Answer options
                val optionLabels = listOf("A", "B", "C", "D")
                items(question!!.options.size) { index ->
                    val option = question!!.options[index]
                    val isCorrect = index == question!!.correctIndex
                    val isSelected = selectedIndex == index

                    val containerColor = when {
                        !isAnswered -> Color(0xFF2A2A2A)
                        isCorrect -> Color(0xFF1B5E20)
                        isSelected && !isCorrect -> Color(0xFF7F0000)
                        else -> Color(0xFF2A2A2A)
                    }
                    val borderColor = when {
                        !isAnswered -> Color(0xFF3A3A3A)
                        isCorrect -> Color(0xFF4CAF50)
                        isSelected && !isCorrect -> Color(0xFFF44336)
                        else -> Color(0xFF3A3A3A)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isAnswered) {
                                selectedIndex = index
                                if (isCorrect) score++
                            },
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Option label badge
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(borderColor.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    optionLabels[index],
                                    color = borderColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                option,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            // Show icon after answering
                            if (isAnswered) {
                                if (isCorrect) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Incorrect",
                                        tint = Color(0xFFF44336),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Next / Finish button
                item {
                    AnimatedVisibility(visible = isAnswered) {
                        Button(
                            onClick = {
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                    selectedIndex = null
                                } else {
                                    showResult = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (currentIndex < questions.size - 1) "Tiếp theo →" else "Xem kết quả 🎯",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Result body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎯", fontSize = 72.sp)
                Text(
                    "Hoàn thành!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Score display
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFF1B3A2D), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$score",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp
                        )
                        Text(
                            "/ ${questions.size}",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val message = when {
                    score < questions.size / 2 -> "Cố lên! Luyện tập thêm nhé 💪"
                    score < (questions.size * 0.8).toInt() -> "Tốt lắm! Bạn đang tiến bộ 👍"
                    else -> "Xuất sắc! Bạn thành thạo rồi 🌟"
                }
                Text(
                    message,
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        questions = generateQuestions(reviewVocabs)
                        currentIndex = 0
                        score = 0
                        selectedIndex = null
                        showResult = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔄 Làm lại", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = {
                        navController.navigate("vocab") {
                            popUpTo("vocab") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50))
                ) {
                    Text(
                        "🏠 Về trang từ vựng",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}











