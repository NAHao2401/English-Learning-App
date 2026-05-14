package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SpeakerIconButton(
    audioUrl: String?,
    baseUrl: String,
    fallbackText: String,
    audioPlayer: VocabAudioPlayer,
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF4CAF50),
    size: Dp = 20.dp
) {
    IconButton(
        onClick = { audioPlayer.play(audioUrl, baseUrl, fallbackText) },
        modifier = modifier.size(size + 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Phát âm",
            tint = tint,
            modifier = Modifier.size(size)
        )
    }
}
