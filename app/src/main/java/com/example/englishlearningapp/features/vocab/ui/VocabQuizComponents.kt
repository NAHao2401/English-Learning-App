package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnswerOptionCard(
    option: String,
    label: String,
    isAnswered: Boolean,
    isSelected: Boolean,
    isCorrect: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (bgColor, borderColor, textColor) = when {
        !isAnswered -> Triple(Color(0xFF2A2A2A), Color(0xFF4A4A4A), Color.White)
        isCorrect -> Triple(Color(0xFF1B5E20), Color(0xFF4CAF50), Color(0xFF4CAF50))
        isSelected -> Triple(Color(0xFF7F0000), Color(0xFFF44336), Color(0xFFF44336))
        else -> Triple(Color(0xFF2A2A2A), Color(0xFF3A3A3A), Color(0xFF6A6A6A))
    }

    Card(
        onClick = onClick,
        enabled = !isAnswered,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.5.dp, borderColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    color = borderColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = option,
                    color = textColor,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = if (isCorrect && isAnswered) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    correctCount: Int,
    totalCount: Int,
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            val emoji = when {
                correctCount == totalCount -> "🎉"
                correctCount >= totalCount * 0.7f -> "👍"
                else -> "💪"
            }
            Text(emoji, fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "Kết quả ôn tập",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFF1B3A2D), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$correctCount",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 40.sp
                    )
                    Text("/ $totalCount", color = Color.Gray, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = when {
                    correctCount == totalCount -> "Hoàn hảo! Bạn nhớ tất cả! 🌟"
                    correctCount >= totalCount * 0.7f -> "Tốt lắm! Tiếp tục cố gắng! 👏"
                    else -> "Luyện tập thêm để nhớ lâu hơn! 💪"
                },
                color = Color.LightGray,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Hoàn thành",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

