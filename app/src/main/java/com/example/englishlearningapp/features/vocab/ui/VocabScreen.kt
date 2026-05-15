package com.example.englishlearningapp.features.vocab.ui

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import com.example.englishlearningapp.data.local.db.entity.UserEntity
import com.example.englishlearningapp.data.remote.api.response.VocabOverviewResponse
import com.example.englishlearningapp.features.usertopic.UserTopicViewModel
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import androidx.core.graphics.toColorInt
import com.example.englishlearningapp.features.usertopic.UserTopicViewModelFactory
import com.example.englishlearningapp.navigation.Screen

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
    val learnedCount: Int,
    val reviewCount: Int,
    val iconRes: Int?
)

@Composable
fun VocabScreen(
    navController: NavController,
    vocabVm: VocabViewModel? = null,
    userTopicVm: UserTopicViewModel? = null
) {
    val context = LocalContext.current
    val viewModel = vocabVm ?: composeViewModel(factory = com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModelFactory(context))
    val userTopicViewModel = userTopicVm ?: composeViewModel(factory = UserTopicViewModelFactory(
        context
    )
    )
    val topics by viewModel.topics.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val savedVocabs by viewModel.savedVocabs.collectAsState()
    val userTopics by userTopicViewModel.userTopics.collectAsState()
    val topicWordCounts by userTopicViewModel.topicWordCounts.collectAsState()
    val topicLearnedCounts by userTopicViewModel.topicLearnedCounts.collectAsState()
    val learnedCountByLevel by viewModel.learnedCountByLevel.collectAsState()
    val vocabOverview by viewModel.vocabOverview.collectAsState()
    val isLoadingOverview by viewModel.isLoadingOverview.collectAsState()
    val savedIds = remember(savedVocabs) { savedVocabs.map { it.id }.toSet() }
    val displayTopicWordCounts = remember(userTopics, topicWordCounts) {
        if (topicWordCounts.isNotEmpty()) topicWordCounts else userTopics.associate { it.id to it.wordCount }
    }
    val totalFolderWords = remember(userTopics, displayTopicWordCounts) {
        userTopics.sumOf { topic -> displayTopicWordCounts[topic.id] ?: topic.wordCount }
    }
    val displayTopicLearnedCounts = remember(userTopics, topicLearnedCounts) {
        if (topicLearnedCounts.isNotEmpty()) topicLearnedCounts else userTopics.associate { it.id to it.learnedCount }
    }
    val totalFolderLearned = remember(userTopics, displayTopicLearnedCounts) {
        userTopics.sumOf { topic -> displayTopicLearnedCounts[topic.id] ?: topic.learnedCount }
    }
    val levelCounts = remember {
        listOf(
            "Mới học" to 0,
            "Nhớ tạm" to 2,
            "Nhớ lâu" to 0,
            "Thuộc lòng" to 0,
            "Thông thạo" to 5
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadVocabOverview()
        viewModel.loadLearnedVocabs()
        userTopicViewModel.loadUserTopics()
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
                    overview    = vocabOverview,
                    isLoading   = isLoadingOverview,
                    onCardClick = {
                        navController.navigateSafely("learned_words") {
                            Toast.makeText(context, "Danh sách từ đã học chưa sẵn sàng", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onStudyClick = {
                        navController.navigate("review_quiz")
                    },
                    onNavigatePractice = { route ->
                        navController.navigate(route)
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
                    savedCount = totalFolderWords,
                    learnedCount = totalFolderLearned,
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
    learnedCount: Int,
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
                                text = "$learnedCount/$savedCount đã học",
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
    val learnedCountByLevel by viewModel.learnedCountByLevel.collectAsState()
    val cefrLevels = remember(vocabCountByLevel) {
        listOf(
            CefrLevel(0, "A0", "Pre\nBeginner", "A0 - Từ Vựng Cho\nNgười Mất Gốc", vocabCountByLevel["A0"] ?: 0, learnedCountByLevel["A0"] ?: 0, 0, null),
            CefrLevel(1, "A1", "Beginner", "Cấp độ A1", vocabCountByLevel["A1"] ?: 0, learnedCountByLevel["A1"] ?: 0, 0, null),
            CefrLevel(2, "A2", "Elementary", "Cấp độ A2", vocabCountByLevel["A2"] ?: 0, learnedCountByLevel["A2"] ?: 0, 0, null),
            CefrLevel(3, "B1", "Intermediate", "Cấp độ B1", vocabCountByLevel["B1"] ?: 0, learnedCountByLevel["B1"] ?: 0, 0, null),
            CefrLevel(4, "B2", "Upper Int.", "Cấp độ B2", vocabCountByLevel["B2"] ?: 0, learnedCountByLevel["B2"] ?: 0, 0, null),
            CefrLevel(5, "C1", "Advanced", "Cấp độ C1", vocabCountByLevel["C1"] ?: 0, learnedCountByLevel["C1"] ?: 0, 0, null),
            CefrLevel(6, "C2", "Mastery", "Cấp độ C2", vocabCountByLevel["C2"] ?: 0, learnedCountByLevel["C2"] ?: 0, 0, null)
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
                val remoteTopicId = topicWithCount.topic.remoteTopicId ?: topicWithCount.topic.id
                val onCardClick = remember(remoteTopicId) {
                    {
                        viewModel.selectTopic(topicWithCount.topic.id)
                        navController.navigateSafely("topic_detail/$remoteTopicId") {
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
    overview      : VocabOverviewResponse?,
    isLoading     : Boolean,
    onCardClick   : () -> Unit,
    onStudyClick  : () -> Unit,
    onNavigatePractice: (route: String) -> Unit
) {
    val learned  = overview?.learnedCount    ?: 0
    val dueCount = overview?.dueReviewCount  ?: 0
    val stats    = overview?.masteryStats
    var showPracticeModeSheet by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = CardBg),
        shape    = RoundedCornerShape(16.dp),
        onClick = onCardClick 
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header row: "N từ đã học" + > arrow ──
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCardClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    // Shimmer placeholder
                    Box(
                        Modifier
                            .width(80.dp).height(28.dp)
                            .background(Color(0xFF3A3A3A), RoundedCornerShape(6.dp))
                    )
                } else {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(
                                color      = PrimaryGreen,
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )) { append("$learned") }
                            withStyle(SpanStyle(
                                color    = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )) { append(" từ đã học") }
                        }
                    )
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                )
            }

            Spacer(Modifier.height(14.dp))

            // ── 5 mastery ring icons ──
            val masteryData = listOf(
                Triple(stats?.level1 ?: 0, "Chưa biết",    1),
                Triple(stats?.level2 ?: 0, "Mới học",      2),
                Triple(stats?.level3 ?: 0, "Nhớ tạm",      3),
                Triple(stats?.level4 ?: 0, "Nhớ lâu",      4),
                Triple(stats?.level5 ?: 0, "Thông thạo",   5),
            )

            val scrollState = rememberScrollState()
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                masteryData.forEach { (count, label, filledSegments) ->
                    MasteryRingIcon(
                        count         = count,
                        label         = label,
                        filledSegments = filledSegments
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Due review section ──
            if (dueCount > 0) {
                // 🌻 "N từ cần luyện tập" orange row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌻", fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "$dueCount từ cần luyện tập",
                        color      = OrangeAccent,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = OrangeAccent
                    )
                }
                Spacer(Modifier.height(10.dp))

                // Orange "Luyện tập" button
                Button(
                    onClick  = { onStudyClick() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Luyện tập",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                "Thông thường",
                                color = Color.White.copy(.75f),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(
                            onClick  = { showPracticeModeSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            } else {
                // 🌻 "Khu vườn tươi tốt" green row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌻", fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Khu vườn của bạn đang tươi tốt",
                        color      = PrimaryGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
                Spacer(Modifier.height(10.dp))

                // Gray "Không có từ nào cần luyện tập" bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color    = Color(0xFF1E1E1E),
                    shape    = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Không có từ nào cần luyện tập",
                            color    = Color(0xFF7A7A7A),
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = null,
                            tint = Color(0xFF7A7A7A)
                        )
                    }
                }
            }
        }
    }

    if (showPracticeModeSheet) {
        PracticeModeBottomSheet(
            onDismiss = { showPracticeModeSheet = false },
            onSelectMode = { route ->
                showPracticeModeSheet = false
                onNavigatePractice(route)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeModeBottomSheet(
    onDismiss    : () -> Unit,
    onSelectMode : (route: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = Color(0xFF2A2A2A),
        dragHandle = {
            Box(
                Modifier
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
                "Chọn cách luyện tập",
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                modifier   = Modifier.padding(vertical = 16.dp)
            )

            HorizontalDivider(color = Color(0xFF3A3A3A))
            Spacer(Modifier.height(16.dp))

            val modes = listOf(
                Triple(
                    "review_quiz",
                    "Luyện tập thông thường",
                    "Trắc nghiệm chọn 1 / 4 đáp án"
                ),
                Triple(
                    "review_quiz_listening",
                    "Luyện tập nghe",
                    "Nghe và chọn nghĩa đúng"
                ),
                Triple(
                    "review_quiz_challenge",
                    "Luyện tập thử thách",
                    "Nhập từ tiếng Anh theo nghĩa"
                )
            )

            modes.forEach { (route, title, subtitle) ->
                Button(
                    onClick  = { onSelectMode(route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(bottom = 10.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF8C00)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                subtitle,
                                color = Color.White.copy(.75f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
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
            Text(
                "Học phải đi đôi với hành.",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "0/$LEARNED_COUNT từ đã đặt câu",
                color = PrimaryGreen,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onSentencePractice,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Luyện đặt câu",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
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
                val strokeWidth  = 4.dp.toPx()
                val segmentSweep = 60f
                val gapSweep     = 12f

                repeat(5) { i ->
                    val startAngle = -90f + i * (segmentSweep + gapSweep)
                    val filled = count > 0

                    drawArc(
                        color      = if (filled) PrimaryGreen else DividerBg,
                        startAngle = startAngle,
                        sweepAngle = segmentSweep,
                        useCenter  = false,
                        style      = Stroke(
                            width = strokeWidth,
                            cap   = StrokeCap.Round
                        )
                    )
                }
            }

            Text(
                text       = count.toString(),
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }

        Spacer(Modifier.height(3.dp))

        Text(
            text     = label,
            color    = Color.Gray,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
            modifier = Modifier.width(56.dp)
        )
    }
}

@Composable
fun MasteryRingIcon(
    count         : Int,
    label         : String,
    filledSegments: Int = 0,
    modifier      : Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = modifier.width(56.dp)
    ) {
        Box(
            modifier         = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth  = 4.dp.toPx()
                val segmentSweep = 60f
                val gapSweep     = 12f

                repeat(5) { i ->
                    val startAngle = -90f + i * (segmentSweep + gapSweep)
                    val filled = i < filledSegments

                    drawArc(
                        color      = if (filled) PrimaryGreen
                                     else DividerBg,
                        startAngle = startAngle,
                        sweepAngle = segmentSweep,
                        useCenter  = false,
                        style      = Stroke(
                            width = strokeWidth,
                            cap   = StrokeCap.Round
                        )
                    )
                }
            }

            Text(
                text       = count.toString(),
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }

        Spacer(Modifier.height(3.dp))

        Text(
            text     = label,
            color    = Color.Gray,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
            modifier  = Modifier.fillMaxWidth()
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
                        Text("${level.learnedCount}/${level.wordCount}", color = Color.LightGray, fontSize = 11.sp)
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
