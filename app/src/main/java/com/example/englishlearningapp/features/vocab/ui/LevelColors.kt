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
    "A0" -> Color(0xFF2A2A2A)
    "A1" -> Color(0xFF1B3A2D)
    "A2" -> Color(0xFF1A3340)
    "B1" -> Color(0xFF1A2E40)
    "B2" -> Color(0xFF2D1B3A)
    "C1" -> Color(0xFF3A2A1A)
    "C2" -> Color(0xFF3A1B1B)
    else -> Color(0xFF2A2A2A)
}

