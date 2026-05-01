package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import com.example.englishlearningapp.features.usertopic.UserTopicViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveToTopicBottomSheet(
    vocab: VocabularyResponse,
    onDismiss: () -> Unit,
    onSaved: () -> Unit = {},
    userTopicViewModel: UserTopicViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val userTopics by userTopicViewModel.userTopics.collectAsState()
    val isSaving by userTopicViewModel.isSaving.collectAsState()
    val isCreating by userTopicViewModel.isCreating.collectAsState()
    val saveSuccess by userTopicViewModel.saveSuccess.collectAsState()
    val saveError by userTopicViewModel.saveError.collectAsState()
    val savedVocabIds by userTopicViewModel.savedVocabIds.collectAsState()

    var selectedTopicId by remember { mutableStateOf<Int?>(null) }
    var showCreateField by remember { mutableStateOf(false) }
    var newTopicName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userTopicViewModel.loadUserTopics()
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess != null) {
            onSaved()
            delay(1200)
            userTopicViewModel.clearSaveFeedback()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF2A2A2A),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
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
            Text(
                text = vocab.word,
                color = Color.White,
                fontWeight = Bold,
                fontSize = 20.sp
            )
            Text(
                text = vocab.meaning,
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF3A3A3A))
            Spacer(Modifier.height(16.dp))

            if (!showCreateField) {
                Button(
                    onClick = { showCreateField = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.SaveAlt, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Thêm vào thư mục mới", color = Color.White, fontWeight = Bold, fontSize = 15.sp)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newTopicName,
                        onValueChange = { newTopicName = it },
                        placeholder = { Text("Tên thư mục mới...", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = Color(0xFF4A4A4A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newTopicName.isNotBlank()) {
                                userTopicViewModel.createTopicAndSave(
                                    topicName = newTopicName,
                                    vocabularyId = vocab.id
                                )
                            }
                        },
                        enabled = newTopicName.isNotBlank() && !isCreating,
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            showCreateField = false
                            newTopicName = ""
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (userTopics.isEmpty()) {
                Text(
                    text = "Chưa có thư mục nào. Tạo thư mục mới để lưu từ!",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            } else {
                LazyColumn {
                    items(userTopics, key = { it.id }) { topic ->
                        val isAlreadySaved = savedVocabIds.contains(vocab.id)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isAlreadySaved) {
                                    selectedTopicId = topic.id
                                }
                                .padding(vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTopicId == topic.id,
                                onClick = { selectedTopicId = topic.id },
                                enabled = !isAlreadySaved,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF4CAF50),
                                    unselectedColor = Color(0xFF6A6A6A)
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(topic.name, color = Color.White, fontSize = 15.sp, fontWeight = Medium)

                            }
                            if (isAlreadySaved) {
                                Badge(containerColor = Color(0xFF1B3A2D)) {
                                    Text("Đã lưu", color = Color(0xFF4CAF50), fontSize = 10.sp)
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFF3A3A3A), thickness = 0.5.dp)
                    }
                }
            }

            AnimatedVisibility(visible = saveError != null) {
                Text(
                    text = saveError ?: "",
                    color = Color(0xFFF44336),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            AnimatedVisibility(visible = saveSuccess != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(saveSuccess ?: "", color = Color(0xFF4CAF50), fontSize = 13.sp)
                }
            }

            AnimatedVisibility(visible = selectedTopicId != null && saveSuccess == null) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedTopicId?.let { topicId ->
                            userTopicViewModel.saveVocabularyToTopic(
                                vocabularyId = vocab.id,
                                userTopicId = topicId
                            )
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Lưu vào thư mục này", color = Color.White, fontWeight = Bold, fontSize = 15.sp)
                }
            }
        }
    }
}





