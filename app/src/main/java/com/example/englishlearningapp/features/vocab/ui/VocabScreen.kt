package com.example.englishlearningapp.features.vocab.ui

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import com.example.englishlearningapp.data.local.db.entity.UserEntity
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import com.example.englishlearningapp.ui.navigation.Screen
import androidx.core.graphics.toColorInt

private val DarkBg = Color(0xFF1A1A1A)
private val CardBg = Color(0xFF2A2A2A)
private val DividerBg = Color(0xFF3A3A3A)
private val PrimaryGreen = Color(0xFF4CAF50)
private val OrangeAccent = Color(0xFFFF8C00)
private const val LEARNED_COUNT = 7
private const val REVIEW_DUE_COUNT = 2

data class CefrLevel(
    val id: Int,
    val levelCode: String,
    val badge: String,
    val name: String,
    val wordCount: Int,
    val reviewCount: Int,
    val iconRes: Int?
)

@Composable
fun VocabScreen(
    navController: NavController,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val topics by viewModel.topics.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val savedVocabs by viewModel.savedVocabs.collectAsState()
    val savedIds = remember(savedVocabs) { savedVocabs.map { it.id }.toSet() }
    val levelCounts = remember {
        listOf(
            "Mới học" to 0,
            "Nhớ tạm" to 2,
            "Nhớ lâu" to 0,
            "Thuộc lòng" to 0,
            "Thông thạo" to 5
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {}
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item(key = "search_section") {
                SearchBarWithResults(
                    viewModel = viewModel,
                    savedIds = savedIds
                )
            }

            item(key = "learning_progress") {
                LearningProgressCard(
                    levelCounts = levelCounts,
                    onLearnedClick = {
                        navController.navigateSafely("learned_words") {
                            Toast.makeText(context, "Danh sách từ đã học chưa sẵn sàng", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onPracticeClick = {
                        navController.navigateSafely("flashcard") {
                            Toast.makeText(context, "Flashcard chưa sẵn sàng", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            item(key = "practice_section") {
                PracticeSectionCard(
                    onSentencePractice = {
                        navController.navigateSafely("sentence_practice") {
                            Toast.makeText(context, "Luyện đặt câu chưa sẵn sàng", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            item(key = "my_folder_section") {
                MyFolderCard(
                    user = currentUser,
                    savedCount = savedVocabs.size,
                    reviewDueCount = 0,
                    onCardClick = { navController.navigate(Screen.UserTopics.route) },
                    onStudyClick = { navController.navigate(Screen.UserTopics.route) }
                )
            }

            // Tạm thời tắt các section gây lag để kiểm tra hiệu năng
            item(key = "cefr_section") {
                CefrSection(
                    navController = navController,
                    context = context,
                    viewModel = viewModel
                )
            }

            item(key = "topic_section") {
                TopicSection(
                    topics = topics,
                    navController = navController,
                    context = context,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun SearchBarWithResults(
    viewModel: VocabViewModel,
    savedIds: Set<Int>
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearch,
            placeholder = { Text("Tìm kiếm từ vựng...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearch("") }) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = Color.Gray)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = CardBg,
                focusedContainerColor = CardBg,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryGreen,
                cursorColor = PrimaryGreen,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Fixed-height container prevents layout thrashing when results appear/disappear
        val showResults = searchQuery.length >= 2
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .background(
                    if (showResults) CardBg else Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
        ) {
            if (showResults && searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    items(
                        items = searchResults,
                        key = { it.id },
                        contentType = { "search_result" }
                    ) { vocab ->
                        val isSaved = savedIds.contains(vocab.id)
                        val onToggleSaveCallback = remember(vocab.id, isSaved) {
                            { viewModel.toggleSave(vocab.id, isSaved) }
                        }
                        SearchResultItem(
                            word = vocab.word,
                            pronunciation = vocab.pronunciation,
                            meaning = vocab.meaning,
                            isSaved = isSaved,
                            onToggleSave = onToggleSaveCallback
                        )
                    }
                }
            } else if (showResults) {
                Text(
                    "Không tìm thấy",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun MyFolderCard(
    user: UserEntity?,
    savedCount: Int,
    reviewDueCount: Int,
    onCardClick: () -> Unit,
    onStudyClick: () -> Unit
) {
    Card(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B3A2D)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!user?.avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = user!!.avatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Thư mục của tôi",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = "0/$savedCount đã học",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = "$reviewDueCount cần luyện tập",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStudyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🃏  Học từ mới",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun CefrSection(
    navController: NavController,
    context: android.content.Context,
    viewModel: VocabViewModel
) {
    val vocabCountByLevel by viewModel.vocabCountByLevel.collectAsState()
    val cefrLevels = remember(vocabCountByLevel) {
        listOf(
            CefrLevel(0, "A0", "Pre\nBeginner", "A0 - Từ Vựng Cho\nNgười Mất Gốc", vocabCountByLevel["A0"] ?: 0, 0, null),
            CefrLevel(1, "A1", "Beginner", "Cấp độ A1", vocabCountByLevel["A1"] ?: 0, 0, null),
            CefrLevel(2, "A2", "Elementary", "Cấp độ A2", vocabCountByLevel["A2"] ?: 0, 0, null),
            CefrLevel(3, "B1", "Intermediate", "Cấp độ B1", vocabCountByLevel["B1"] ?: 0, 0, null),
            CefrLevel(4, "B2", "Upper Int.", "Cấp độ B2", vocabCountByLevel["B2"] ?: 0, 0, null),
            CefrLevel(5, "C1", "Advanced", "Cấp độ C1", vocabCountByLevel["C1"] ?: 0, 0, null),
            CefrLevel(6, "C2", "Mastery", "Cấp độ C2", vocabCountByLevel["C2"] ?: 0, 0, null)
        )
    }
    val onHeaderClick = remember {
        {
            navController.navigateSafely("cefr_list") {
                Toast.makeText(context, "Danh mục CEFR chưa sẵn sàng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "Từ vựng CEFR",
            subtitle = "${cefrLevels.size} thư mục",
            onClick = onHeaderClick
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(
                items = cefrLevels,
                key = { it.id },
                contentType = { "cefr" }
            ) { level ->
                val onCardClick = remember(level.levelCode) {
                    {
                        navController.navigateSafely(Screen.CefrLevelDetail.createRoute(level.levelCode)) {
                            Toast.makeText(context, "Chi tiết CEFR chưa sẵn sàng", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                CefrLevelCard(level = level, onClick = onCardClick)
            }
        }
    }
}

@Composable
private fun TopicSection(
    topics: List<TopicWithCount>,
    navController: NavController,
    context: android.content.Context,
    viewModel: VocabViewModel
) {
    var displayedTopicCount by remember { mutableStateOf(8) }
    val paginatedTopics = remember(topics, displayedTopicCount) { topics.take(displayedTopicCount) }
    val onHeaderClick = remember {
        {
            navController.navigateSafely("topics") {
                Toast.makeText(context, "Danh mục chủ đề chưa sẵn sàng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "Từ vựng theo chủ đề",
            subtitle = "${topics.size} thư mục",
            onClick = onHeaderClick
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(
                items = paginatedTopics,
                key = { it.topic.id },
                contentType = { "topic" }
            ) { topicWithCount ->
                val onCardClick = remember(topicWithCount.topic.id) {
                    {
                        viewModel.selectTopic(topicWithCount.topic.id)
                        navController.navigateSafely("topic_detail/${topicWithCount.topic.id}") {
                            Toast.makeText(context, "Chi tiết chủ đề chưa sẵn sàng", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                TopicCard(topicWithCount = topicWithCount, onClick = onCardClick)
            }
        }

        if (paginatedTopics.size < topics.size) {
            Button(
                onClick = { displayedTopicCount += 8 },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Xem thêm chủ đề", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LearningProgressCard(
    levelCounts: List<Pair<String, Int>>,
    onLearnedClick: () -> Unit,
    onPracticeClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onLearnedClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$LEARNED_COUNT từ đã học",
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(
                    items = levelCounts,
                    key = { it.first },
                    contentType = { "progress_ring" }
                ) { (label, count) ->
                    CircularProgressRing(label = label, count = count)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🌻", fontSize = 20.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "$REVIEW_DUE_COUNT từ cần luyện tập",
                    color = OrangeAccent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Info, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(16.dp))
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onPracticeClick,
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Luyện tập", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Thẻ ghi nhớ", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun PracticeSectionCard(
    onSentencePractice: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Học phải đi đôi với hành.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text("0/$LEARNED_COUNT từ đã đặt câu", color = PrimaryGreen, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onSentencePractice,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Luyện đặt câu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(subtitle, color = Color.Gray, fontSize = 13.sp)
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onClick) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun CircularProgressRing(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 4.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                drawCircle(
                    color = DividerBg,
                    radius = radius,
                    style = Stroke(width = strokeWidth)
                )
                drawCircle(
                    color = PrimaryGreen,
                    radius = radius,
                    style = Stroke(width = strokeWidth)
                )
            }
            Text(
                text = count.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(52.dp)
        )
    }
}

@Composable
fun CefrLevelCard(level: CefrLevel, onClick: () -> Unit) {
    val bgColor = remember(level.id) {
        when (level.id) {
            0 -> Color(0xFF555555)
            1 -> Color(0xFF1E4C31)
            2 -> Color(0xFF16566A)
            3 -> Color(0xFF1A3E66)
            4 -> Color(0xFF472066)
            5 -> Color(0xFF6A4315)
            else -> Color(0xFF6A1B1B)
        }
    }
    val badgeColor = remember(level.badge) { cefrBadgeColor(level.badge) }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(160.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = level.badge,
                color = badgeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 14.sp
            )
            Column {
                Text(
                    text = level.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("1/${level.wordCount}", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NightsStay, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("${level.reviewCount}", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun TopicCard(topicWithCount: TopicWithCount, onClick: () -> Unit) {
    val bgColor = remember(topicWithCount.topic.level) {
        when (topicWithCount.topic.level) {
            "A0" -> Color(0xFF2A2A2A)
            "A1" -> Color(0xFF1B3A2D)
            "A2" -> Color(0xFF1A3340)
            "B1" -> Color(0xFF1A2E40)
            "B2" -> Color(0xFF2D1B3A)
            "C1" -> Color(0xFF3A2A1A)
            "C2" -> Color(0xFF3A1B1B)
            else -> CardBg
        }
    }

    val topicIconText = remember(topicWithCount.topic.iconUrl) { topicWithCount.topic.iconUrl?.takeUnless { it.startsWith("#") } ?: "📚" }
    val topicAccentColor = remember(topicWithCount.topic.iconUrl, topicWithCount.topic.level) {
        topicWithCount.topic.iconUrl
            ?.takeIf { it.startsWith("#") }
            ?.let { Color(it.toColorInt()) }
            ?: levelCodeColor(topicWithCount.topic.level)
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(160.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = topicWithCount.topic.level ?: "",
                color = levelCodeColor(topicWithCount.topic.level),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .background(topicAccentColor.copy(alpha = 0.28f), RoundedCornerShape(999.dp))
            )
            Text(
                text = topicIconText,
                fontSize = 40.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .alpha(0.6f)
            )
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = topicWithCount.topic.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("0/${topicWithCount.wordCount}", color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    word: String,
    pronunciation: String?,
    meaning: String,
    isSaved: Boolean,
    onToggleSave: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(word, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(pronunciation ?: "", color = Color.Gray, fontSize = 12.sp)
            }
            Text(
                meaning,
                color = Color.LightGray,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onToggleSave) {
                Icon(
                    if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = if (isSaved) PrimaryGreen else Color.Gray
                )
            }
        }
        HorizontalDivider(color = DividerBg, thickness = 0.5.dp)
    }
}

fun cefrBadgeColor(badge: String): Color = when {
    badge.contains("Beginner", ignoreCase = true) -> PrimaryGreen
    badge.contains("Elementary", ignoreCase = true) -> Color(0xFF00BCD4)
    badge.contains("Intermediate", ignoreCase = true) -> Color(0xFF2196F3)
    badge.contains("Upper", ignoreCase = true) -> Color(0xFF9C27B0)
    badge.contains("Advanced", ignoreCase = true) -> Color(0xFFFF9800)
    badge.contains("Mastery", ignoreCase = true) -> Color(0xFFF44336)
    else -> Color.Gray
}

private fun NavController.navigateSafely(route: String, onUnavailable: () -> Unit) {
    runCatching { navigate(route) }
        .onFailure { onUnavailable() }
}
