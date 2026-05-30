package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun vocabIsDarkTheme(): Boolean =
    MaterialTheme.colorScheme.background.luminance() < 0.5f

@Composable
fun vocabScreenBackground(): Color =
    MaterialTheme.colorScheme.background

@Composable
fun vocabPanelBackground(): Color =
    MaterialTheme.colorScheme.surface

@Composable
fun vocabBackgroundBrush(): Brush {
    return Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun vocabDividerColor(): Color =
    if (vocabIsDarkTheme()) Color(0xFF2E2E2E) else Color(0xFFE2E7E4)

@Composable
fun vocabAccent(): Color =
    if (vocabIsDarkTheme()) Color(0xFF4CAF50) else Color(0xFF2F7D62)

@Composable
fun vocabAccentText(): Color =
    if (vocabIsDarkTheme()) Color(0xFF66BB6A) else Color(0xFF25684F)

@Composable
fun vocabSoftAccent(): Color =
    if (vocabIsDarkTheme()) Color(0xFF173A27) else Color(0xFFEAF4EF)

@Composable
fun vocabWarmAccent(): Color =
    if (vocabIsDarkTheme()) Color(0xFFFF8C00) else Color(0xFFC97A18)

@Composable
fun vocabPrimaryAction(): Color =
    if (vocabIsDarkTheme()) Color(0xFF1565C0) else Color(0xFF3F6F8F)

@Composable
fun vocabCardContainer(): Color =
    MaterialTheme.colorScheme.surface

@Composable
fun vocabLevelCardContainer(level: String?): Color = if (vocabIsDarkTheme()) {
    when (level) {
        "A0" -> Color(0xFF242424)
        "A1" -> Color(0xFF172A20)
        "A2" -> Color(0xFF12292D)
        "B1" -> Color(0xFF152638)
        "B2" -> Color(0xFF271D33)
        "C1" -> Color(0xFF302518)
        "C2" -> Color(0xFF321D1D)
        else -> MaterialTheme.colorScheme.surface
    }
} else {
    when (level) {
        "A0" -> Color(0xFFF2F1EE)
        "A1" -> Color(0xFFEAF4EF)
        "A2" -> Color(0xFFEAF5F6)
        "B1" -> Color(0xFFEAF1F7)
        "B2" -> Color(0xFFF0ECF6)
        "C1" -> Color(0xFFF7F0E6)
        "C2" -> Color(0xFFF7EDED)
        else -> MaterialTheme.colorScheme.surface
    }
}
