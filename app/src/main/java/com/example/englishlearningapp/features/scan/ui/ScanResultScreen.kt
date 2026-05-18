package com.example.englishlearningapp.features.scan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.features.scan.viewmodel.ScanViewModel

@Composable
fun ScanResultScreen(
    viewModel: ScanViewModel,
    onBack: () -> Unit,
    onScanAgain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            // Optional: navigate về home hoặc vocab list
            onBack()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
            Text(
                "Từ vựng tìm được (${uiState.extractedWords.size})",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.extractedWords) { word ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(word.word, style = MaterialTheme.typography.titleMedium)
                        Text(word.meaningVi, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (word.example.isNotBlank()) {
                            Text("\"${word.example}\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Buttons hành động
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onScanAgain, modifier = Modifier.weight(1f)) {
                Text("Scan lại")
            }
            Button(
                onClick = { viewModel.saveAllWords() },
                enabled = !uiState.isSaving && !uiState.isSaveSuccess,
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else if (uiState.isSaveSuccess) {
                    Text("Đã lưu!")
                } else {
                    Text("Lưu tất cả")
                }
            }
        }
    }
}