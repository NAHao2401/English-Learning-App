package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.ui.graphics.Color

fun levelCodeColor(level: String?): Color = when (level) {
    "A0" -> Color(0xFF9E9E9E)
    "A1" -> Color(0xFF4CAF50)
    "A2" -> Color(0xFF00BCD4)
    "B1" -> Color(0xFF2196F3)
    "B2" -> Color(0xFF9C27B0)
    "C1" -> Color(0xFFFF9800)
    "C2" -> Color(0xFFF44336)
    else -> Color(0xFF9E9E9E)
}

fun levelBgColor(level: String?): Color = when (level) {
    "A0" -> Color(0xFFF1F1F1)
    "A1" -> Color(0xFFE8F5E9)
    "A2" -> Color(0xFFE0F7FA)
    "B1" -> Color(0xFFE3F2FD)
    "B2" -> Color(0xFFF3E5F5)
    "C1" -> Color(0xFFFFF3E0)
    "C2" -> Color(0xFFFFEBEE)
    else -> Color(0xFFF5F5F5)
}

