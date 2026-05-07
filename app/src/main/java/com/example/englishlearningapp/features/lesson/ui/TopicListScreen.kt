package com.example.englishlearningapp.features.lesson.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.remote.api.response.TopicResponse

private data class TopicVisual(
    val icon: ImageVector,
    val accent: Color,
    val softAccent: Color,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicListScreen(
    topics: List<TopicResponse>,
    isLoading: Boolean,
    errorMessage: String?,
    onTopicClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8F6FF),
            Color(0xFFF6F9FF),
            Color(0xFFFFFFFF)
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Topics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B2F)
                        )
                        Text(
                            text = "Pick your learning path",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF77738A)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF232136)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
        ) {
            when {
                isLoading && topics.isEmpty() -> LoadingContent()
                errorMessage != null && topics.isEmpty() -> ErrorContent(message = errorMessage)
                topics.isEmpty() -> EmptyContent()
                else -> TopicListContent(
                    topics = topics,
                    errorMessage = errorMessage,
                    onTopicClick = onTopicClick
                )
            }
        }
    }
}

@Composable
private fun TopicListContent(
    topics: List<TopicResponse>,
    errorMessage: String?,
    onTopicClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PremiumHeroCard(topicCount = topics.size)
        }

        if (errorMessage != null) {
            item {
                InlineErrorCard(message = errorMessage)
            }
        }

        item {
            SectionHeader()
        }

        itemsIndexed(topics) { index, topic ->
            PremiumTopicCard(
                topic = topic,
                visual = topicVisualFor(topic.name, index),
                index = index,
                onClick = { onTopicClick(topic.id) }
            )
        }
    }
}

@Composable
private fun PremiumHeroCard(topicCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF6C63FF),
                            Color(0xFF8E7DFF),
                            Color(0xFFB993FF)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
            )

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
            )

            Column(
                modifier = Modifier.fillMaxWidth(0.86f)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
                ) {
                    Text(
                        text = "$topicCount topics available",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "What do you want to learn today?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Choose a topic and build vocabulary through short, focused English lessons.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.88f),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
    }
}

@Composable
private fun SectionHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Learning categories",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1D1B2F),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Start with the topic you need most",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF7B778C)
            )
        }
    }
}

@Composable
private fun PremiumTopicCard(
    topic: TopicResponse,
    visual: TopicVisual,
    index: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(150.dp)
                    .align(Alignment.CenterStart)
                    .background(visual.accent)
            )

            Column(
                modifier = Modifier.padding(start = 22.dp, end = 18.dp, top = 18.dp, bottom = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(visual.softAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = topic.name,
                            tint = visual.accent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = topic.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF242235),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFF4F2FF)
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF6C63FF),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = topic.description?.takeIf { it.isNotBlank() }
                                ?: "Practice useful English vocabulary and expressions in this topic.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6E6A7D),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopicPill(
                            text = topic.level ?: "Unknown",
                            background = visual.softAccent,
                            content = visual.accent
                        )

                        TopicPill(
                            text = visual.tag,
                            background = Color(0xFFF5F4FA),
                            content = Color(0xFF6E6A7D)
                        )
                    }

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = visual.accent,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Start",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicPill(
    text: String,
    background: Color,
    content: Color
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = content,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF6C63FF))
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Loading topics...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6E6A7D)
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No topics yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF242235)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Topics will appear here once they are available from the backend.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6E6A7D)
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        InlineErrorCard(message = message)
    }
}

@Composable
private fun InlineErrorCard(message: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFFEDEC),
        border = BorderStroke(1.dp, Color(0xFFFFC7C2)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB3261E)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8C1D18)
            )
        }
    }
}

private fun topicVisualFor(topicName: String, index: Int): TopicVisual {
    val normalizedName = topicName.lowercase()

    return when {
        "food" in normalizedName -> TopicVisual(
            icon = Icons.Rounded.Restaurant,
            accent = Color(0xFFFF7A59),
            softAccent = Color(0xFFFFE5DD),
            tag = "Daily words"
        )

        "travel" in normalizedName -> TopicVisual(
            icon = Icons.Rounded.FlightTakeoff,
            accent = Color(0xFF3A86FF),
            softAccent = Color(0xFFE0ECFF),
            tag = "Useful phrases"
        )

        "daily" in normalizedName || "communication" in normalizedName -> TopicVisual(
            icon = Icons.Rounded.WavingHand,
            accent = Color(0xFF7B61FF),
            softAccent = Color(0xFFEAE5FF),
            tag = "Speaking"
        )

        "family" in normalizedName -> TopicVisual(
            icon = Icons.Rounded.FamilyRestroom,
            accent = Color(0xFFFFB020),
            softAccent = Color(0xFFFFF0CC),
            tag = "Relationships"
        )

        "school" in normalizedName -> TopicVisual(
            icon = Icons.Rounded.School,
            accent = Color(0xFF00A878),
            softAccent = Color(0xFFDDF7EF),
            tag = "Study"
        )

        "work" in normalizedName -> TopicVisual(
            icon = Icons.Rounded.Work,
            accent = Color(0xFF5C6BC0),
            softAccent = Color(0xFFE3E7FF),
            tag = "Career"
        )

        else -> {
            val fallback = listOf(
                TopicVisual(Icons.Rounded.Restaurant, Color(0xFFFF7A59), Color(0xFFFFE5DD), "Practice"),
                TopicVisual(Icons.Rounded.FlightTakeoff, Color(0xFF3A86FF), Color(0xFFE0ECFF), "Explore"),
                TopicVisual(Icons.Rounded.WavingHand, Color(0xFF7B61FF), Color(0xFFEAE5FF), "Conversation"),
                TopicVisual(Icons.Rounded.School, Color(0xFF00A878), Color(0xFFDDF7EF), "Skills")
            )
            fallback[index % fallback.size]
        }
    }
}
