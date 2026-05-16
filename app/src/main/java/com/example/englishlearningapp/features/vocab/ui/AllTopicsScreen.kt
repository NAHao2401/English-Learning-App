package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.core.graphics.toColorInt
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.navigation.Screen
import kotlin.math.ceil

private val AllTopicsBg = Color(0xFF1A1A1A)
private val AllTopicsCardBg = Color(0xFF2A2A2A)
private val AllTopicsDivider = Color(0xFF3A3A3A)
private val AllTopicsPrimary = Color(0xFF4CAF50)

private data class CefrLevelItem(
    val code: String,
    val label: String,
    val emoji: String,
    val color: String,
    val sortOrder: Int,
    val wordCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTopicsScreen(
    navController: NavController,
    viewModel: VocabViewModel
) {
    val topics by viewModel.topics.collectAsState()
    val vocabCountByLevel by viewModel.vocabCountByLevel.collectAsState()

    var cefrExpanded by remember { mutableStateOf(true) }
    var topicExpanded by remember { mutableStateOf(true) }

    val cefrLevels = remember(vocabCountByLevel) {
        listOf(
            CefrLevelItem("A0", "Mất Gốc", "🔤", "#9E9E9E", 0),
            CefrLevelItem("A1", "Beginner", "🟢", "#4CAF50", 1),
            CefrLevelItem("A2", "Elementary", "🔵", "#00BCD4", 2),
            CefrLevelItem("B1", "Intermediate", "💙", "#2196F3", 3),
            CefrLevelItem("B2", "Upper Int.", "🟣", "#9C27B0", 4),
            CefrLevelItem("C1", "Advanced", "🟠", "#FF9800", 5),
            CefrLevelItem("C2", "Mastery", "🔴", "#F44336", 6)
        ).map { item ->
            item.copy(wordCount = vocabCountByLevel[item.code] ?: 0)
        }
    }
    val topicRows = remember(topics) { topics.chunked(2) }

    Scaffold(
        containerColor = AllTopicsBg,
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0),
                title = {
                    Text(
                        text = "Tất cả chủ đề",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AllTopicsBg
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                SectionHeader(
                    title = "Từ vựng CEFR",
                    subtitle = "${cefrLevels.size} cấp độ",
                    expanded = cefrExpanded,
                    onToggle = { cefrExpanded = !cefrExpanded }
                )
            }

            if (cefrExpanded) {
                item {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val gridHeight = remember(cefrLevels.size) {
                            calculateGridHeight(
                                itemCount = cefrLevels.size
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gridHeight),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            userScrollEnabled = false
                        ) {
                            items(
                                items = cefrLevels,
                                key = { it.code }
                            ) { item ->
                                AllTopicsCefrCard(
                                    item = item,
                                    onClick = {
                                        navController.navigate(
                                            Screen.CefrLevelDetail.createRoute(item.code)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader(
                    title = "Từ vựng theo chủ đề",
                    subtitle = "${topics.size} thư mục",
                    expanded = topicExpanded,
                    onToggle = { topicExpanded = !topicExpanded }
                )
            }

            if (topicExpanded) {
                items(
                    count = topicRows.size,
                    key = { index -> topicRows[index].first().topic.id }
                ) { index ->
                    val rowItems = topicRows[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { topicWithCount ->
                            val remoteTopicId = topicWithCount.topic.remoteTopicId ?: topicWithCount.topic.id
                            Box(modifier = Modifier.weight(1f)) {
                                AllTopicsTopicCard(
                                    topicWithCount = topicWithCount,
                                    onClick = {
                                        navController.navigate("topic_detail/$remoteTopicId")
                                    }
                                )
                            }
                        }

                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        label = "section_arrow"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = subtitle,
                color = AllTopicsPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation)
        )
    }
}

@Composable
private fun AllTopicsCefrCard(
    item: CefrLevelItem,
    onClick: () -> Unit
) {
    val bgColor = cefrCardBgColor(item.code)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = item.emoji,
                fontSize = 52.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .alpha(0.25f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.code,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = item.label,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Cấp độ ${item.code}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "0/${item.wordCount}",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(13.dp),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "0",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllTopicsTopicCard(
    topicWithCount: TopicWithCount,
    onClick: () -> Unit
) {
    val topic = topicWithCount.topic
    val bgColor = topicCardBgColor(topic.level)
    val topicIconText = remember(topic.iconUrl) {
        topic.iconUrl?.takeUnless { it.startsWith("#") } ?: "📚"
    }
    val topicAccentColor = remember(topic.iconUrl, topic.level) {
        topic.iconUrl
            ?.takeIf { it.startsWith("#") }
            ?.let { Color(it.toColorInt()) }
            ?: levelCodeColor(topic.level)
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = topicIconText,
                fontSize = 52.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .alpha(0.3f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                if (!topic.level.isNullOrBlank()) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = topic.level,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = topic.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "0/${topicWithCount.wordCount}",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(13.dp),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "0",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .padding(2.dp)
                    .background(topicAccentColor.copy(alpha = 0.28f), RoundedCornerShape(999.dp))
            )
        }
    }
}

private fun calculateGridHeight(
    itemCount: Int,
    columns: Int = 2,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 8.dp,
    spacing: Dp = 12.dp,
    itemHeight: Dp = 152.dp
): Dp {
    if (itemCount <= 0) return 0.dp

    val rows = ceil(itemCount / columns.toDouble()).toInt()
    return (verticalPadding * 2) + (itemHeight * rows.toFloat()) + (spacing * (rows - 1).coerceAtLeast(0).toFloat())
}

private fun cefrCardBgColor(code: String): Color = when (code) {
    "A0" -> Color(0xFF424242)
    "A1" -> Color(0xFF2E7D32)
    "A2" -> Color(0xFF00695C)
    "B1" -> Color(0xFF1565C0)
    "B2" -> Color(0xFF6A1B9A)
    "C1" -> Color(0xFFE65100)
    "C2" -> Color(0xFFC62828)
    else -> Color(0xFF2A2A2A)
}

private fun topicCardBgColor(level: String?): Color = when (level) {
    "A0" -> Color(0xFF37474F)
    "A1" -> Color(0xFF1B5E20)
    "A2" -> Color(0xFF004D40)
    "B1" -> Color(0xFF0D47A1)
    "B2" -> Color(0xFF4A148C)
    "C1" -> Color(0xFFBF360C)
    "C2" -> Color(0xFFB71C1C)
    else -> Color(0xFF1A2A3A)
}
