package com.example.englishlearningapp.features.vocab.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.englishlearningapp.features.vocab.viewmodel.CefrLevelViewModel
import com.example.englishlearningapp.features.vocab.viewmodel.TopicProgressItem
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import java.text.Normalizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CefrLevelDetailScreen(
    navController: NavController,
    level: String,
    viewModel: CefrLevelViewModel = hiltViewModel(),
    vocabViewModel: VocabViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val topicItems by viewModel.topicItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val totalWords by viewModel.totalWords.collectAsState()
    val totalLearned by viewModel.totalLearned.collectAsState()
    val localTopics by vocabViewModel.topics.collectAsState()

    LaunchedEffect(level) { viewModel.loadLevel(level) }

    fun levelDisplayName(level: String): String = when (level) {
        "A0" -> "Cấp độ A0 - Mất Gốc"
        "A1" -> "Cấp độ A1"
        "A2" -> "Cấp độ A2"
        "B1" -> "Cấp độ B1"
        "B2" -> "Cấp độ B2"
        "C1" -> "Cấp độ C1"
        "C2" -> "Cấp độ C2"
        else -> "Cấp độ $level"
    }

    fun cefrLevelColor(level: String): Color = when (level) {
        "A0" -> Color(0xFF9E9E9E)
        "A1" -> Color(0xFF4CAF50)
        "A2" -> Color(0xFF00BCD4)
        "B1" -> Color(0xFF2196F3)
        "B2" -> Color(0xFF9C27B0)
        "C1" -> Color(0xFFFF9800)
        "C2" -> Color(0xFFF44336)
        else -> Color(0xFF4CAF50)
    }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            TopAppBar(
                expandedHeight = 84.dp,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back", tint = Color.White)
                    }
                },
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .border(2.5.dp, Color.White, CircleShape)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B3A2D)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = levelDisplayName(level),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        color = Color(0xFF2E7D32),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("$totalLearned/$totalWords đã học", color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                    
                                }
                            }
                        }
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFF1A1A1A))) {

            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF4CAF50))
                            Spacer(Modifier.height(12.dp))
                            Text("Đang tải...", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }

                error != null -> {
                    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("⚠️", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(error ?: "", color = Color.Gray, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadLevel(level) }, colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))) { Text("Thử lại") }
                    }
                }

                topicItems.isEmpty() && !isLoading -> {
                    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("📭", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Chưa có chủ đề nào ở cấp độ $level", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 32.dp,
                            bottom = 32.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        items(items = topicItems, key = { it.topic.id }) { item ->
                            val normalize = { value: String? ->
                                if (value.isNullOrBlank()) {
                                    ""
                                } else {
                                    Normalizer.normalize(value, Normalizer.Form.NFD)
                                        .replace("\\p{M}+".toRegex(), "")
                                        .replace("[^a-zA-Z0-9]+".toRegex(), "")
                                        .lowercase()
                                }
                            }

                            val targetName = normalize(item.topic.name)
                            val targetLevel = (item.topic.level ?: "").uppercase()

                            val resolvedLocalTopic = localTopics
                                .firstOrNull {
                                    normalize(it.topic.name) == targetName &&
                                        (it.topic.level ?: "").uppercase() == targetLevel
                                }
                                ?.topic

                            val resolvedTopicId = resolvedLocalTopic?.remoteTopicId ?: resolvedLocalTopic?.id

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CefrTopicCircleItem(
                                    item = item,
                                    level = level,
                                    onClick = {
                                        if (resolvedTopicId != null) {
                                            navController.navigate("topic_detail/$resolvedTopicId")
                                        } else {
                                            Toast.makeText(context, "Không map được topic local. Vui lòng đồng bộ lại dữ liệu.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CefrTopicCircleItem(item: TopicProgressItem, level: String, onClick: () -> Unit) {
    val progress = if (item.totalWords > 0) item.learnedCount.toFloat() / item.totalWords.toFloat() else 0f

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(200.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)) {
        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
            // Background track ring (dark green)
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF2A3A2A),
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round
            )
            // Progress ring (green arc) — transparent track so background ring shows
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF4CAF50),
                trackColor = Color.Transparent,
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round
            )

            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFF2A2A2A)), contentAlignment = Alignment.Center) {
                val emoji = item.topic.iconEmoji ?: ""
                if (emoji.isNotBlank()) {
                    Text(text = emoji, fontSize = 42.sp)
                } else {
                    Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF5A5A5A), modifier = Modifier.size(44.dp))
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(text = item.topic.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp), contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("${item.learnedCount}/${item.totalWords}", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            
        }
    }
}



