package com.example.englishlearningapp.features.usertopic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfPracticeModeBottomSheet(
    onDismiss: () -> Unit,
    onSelectMode: (route: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF2A2A2A),
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp).height(4.dp)
                    .background(Color(0xFF4A4A4A), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))

            val modes = listOf(
                Triple("self_practice_normal", "Tự luyện tập thông thường", "Trắc nghiệm chọn 1 / 4 đáp án"),
                Triple("self_practice_listening", "Tự luyện tập nghe", "Nghe và chọn nghĩa đúng"),
                Triple("self_practice_challenge", "Tự luyện tập thử thách", "Nhập từ tiếng Anh theo nghĩa")
            )

            modes.forEach { (route, title, subtitle) ->
                Button(
                    onClick = { onSelectMode(route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(bottom = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.White, modifier = Modifier.width(24.dp))
                        androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
                        androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                            androidx.compose.material3.Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                            androidx.compose.material3.Text(subtitle, color = Color(0xFFCCCCCC), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
