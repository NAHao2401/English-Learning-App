package com.example.englishlearningapp.features.home.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

data class HomeFeatureItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isAvailable: Boolean = true,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    userName: String = "Learner",
    totalXp: Int = 0,
    streakCount: Int = 0,
    completedLessons: Int = 0,
    totalLessons: Int = 0,
    completionPercent: Int = 0,
    currentLevel: String = "Beginner",
    isProgressLoading: Boolean = false,
    progressErrorMessage: String? = null,
    onLessonsClick: () -> Unit = {},
    onVocabularyClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    onAiScanClick: () -> Unit = {},
    onSpeakingClick: () -> Unit = {},
    onContinueLearningClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val featureItems = listOf(
        HomeFeatureItem(
            title = "Lessons",
            subtitle = "Learn by topics",
            icon = Icons.Default.AutoStories,
            onClick = onLessonsClick
        ),
        HomeFeatureItem(
            title = "Vocabulary",
            subtitle = "Word practice",
            icon = Icons.Default.Translate,
            isAvailable = false,
            onClick = onVocabularyClick
        ),
        HomeFeatureItem(
            title = "Progress",
            subtitle = "View your stats",
            icon = Icons.Default.BarChart,
            onClick = onProgressClick
        ),
        HomeFeatureItem(
            title = "AI Scan",
            subtitle = "Coming soon",
            icon = Icons.Default.CameraAlt,
            isAvailable = false,
            onClick = onAiScanClick
        ),
        HomeFeatureItem(
            title = "Speaking",
            subtitle = "Coming soon",
            icon = Icons.Default.Mic,
            isAvailable = false,
            onClick = onSpeakingClick
        )
    )

    val safeCompletionPercent = completionPercent.coerceIn(0, 100)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF8F6FF),
            Color(0xFFF6F9FF),
            Color(0xFFFFFFFF)
        )
    )

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 18.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                HomeHeader(
                    userName = userName,
                    onLogoutClick = onLogoutClick
                )
            }

            item {
                HomeHeroCard(
                    currentLevel = currentLevel,
                    totalXp = totalXp,
                    completionPercent = safeCompletionPercent,
                    completedLessons = completedLessons,
                    totalLessons = totalLessons,
                    isLoading = isProgressLoading,
                    onContinueLearningClick = onContinueLearningClick
                )
            }

            if (progressErrorMessage != null) {
                item {
                    InlineInfoCard(message = progressErrorMessage)
                }
            }

            item {
                StatsSection(
                    totalXp = totalXp,
                    streakCount = streakCount,
                    completedLessons = completedLessons,
                    completionPercent = safeCompletionPercent
                )
            }

            item {
                ContinueLearningCard(
                    completedLessons = completedLessons,
                    totalLessons = totalLessons,
                    onContinueLearningClick = onContinueLearningClick
                )
            }

            item {
                SectionTitle(
                    title = "Explore",
                    subtitle = "Choose what you want to practice today"
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    featureItems.chunked(2).forEach { rowItems ->
                        if (rowItems.size == 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                FeatureCard(
                                    item = rowItems.first(),
                                    modifier = Modifier.fillMaxWidth(0.48f)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowItems.forEach { item ->
                                    FeatureCard(
                                        item = item,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                DailyMotivationCard(streakCount = streakCount)
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello, ${userName.ifBlank { "Learner" }} 👋",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1D1B2F),
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ready for a quick English boost today?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF77738A)
            )
        }

        LogoutPill(onClick = onLogoutClick)
    }
}

@Composable
private fun LogoutPill(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.88f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = Color(0xFF6C63FF),
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF3B356F),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HomeHeroCard(
    currentLevel: String,
    totalXp: Int,
    completionPercent: Int,
    completedLessons: Int,
    totalLessons: Int,
    isLoading: Boolean,
    onContinueLearningClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
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
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
            )

            Column(modifier = Modifier.fillMaxWidth(0.90f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD166).copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(26.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFD166),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Level $currentLevel",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$totalXp XP earned",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Keep your learning momentum",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (totalLessons > 0) {
                        "$completedLessons of $totalLessons lessons completed • $completionPercent% done"
                    } else {
                        "Start your first lesson and build a daily habit"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.90f)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(onClick = onContinueLearningClick) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (completedLessons > 0) "Continue learning" else "Start learning")
                }
            }
        }
    }
}

@Composable
private fun StatsSection(
    totalXp: Int,
    streakCount: Int,
    completedLessons: Int,
    completionPercent: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "XP",
                value = totalXp.toString(),
                icon = Icons.Default.Star,
                iconTint = Color(0xFFFFB300),
                iconBackground = Color(0xFFFFF3D6)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Streak",
                value = "$streakCount days",
                icon = Icons.Default.LocalFireDepartment,
                iconTint = Color(0xFFFF6D00),
                iconBackground = Color(0xFFFFE3D3)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Lessons",
                value = completedLessons.toString(),
                icon = Icons.Default.CheckCircle,
                iconTint = Color(0xFF4F7DFF),
                iconBackground = Color(0xFFE8EEFF)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Complete",
                value = "$completionPercent%",
                icon = Icons.Default.BarChart,
                iconTint = Color(0xFF7C5CFF),
                iconBackground = Color(0xFFEDE8FF)
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1D1B2F),
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF77738A),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ContinueLearningCard(
    completedLessons: Int,
    totalLessons: Int,
    onContinueLearningClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6E8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFFA726).copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = Color(0xFFFF8A00),
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Today's practice",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2B2118),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (totalLessons > 0) {
                        "You have ${totalLessons - completedLessons} lessons left to explore."
                    } else {
                        "Pick a topic and complete your first lesson."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A604A)
                )
            }

            TextButton(onClick = onContinueLearningClick) {
                Text("Go")
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF1D1B2F),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF77738A)
        )
    }
}

@Composable
private fun FeatureCard(
    item: HomeFeatureItem,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .height(132.dp)
            .clickable(enabled = item.isAvailable) { item.onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (item.isAvailable) Color.White else Color(0xFFF1F1F6)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (item.isAvailable) 3.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (item.isAvailable) {
                                Color(0xFF6C63FF).copy(alpha = 0.12f)
                            } else {
                                Color(0xFF77738A).copy(alpha = 0.12f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (item.isAvailable) Color(0xFF6C63FF) else Color(0xFF77738A)
                    )
                }

                if (!item.isAvailable) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Coming soon",
                        tint = Color(0xFFAAA6B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.isAvailable) Color(0xFF1D1B2F) else Color(0xFF77738A),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF77738A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DailyMotivationCard(streakCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF9F2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE3D3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFFFF6D00)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (streakCount > 0) "Great streak: $streakCount days" else "Build your first streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF173B24),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Practice a little every day, and your English will grow faster.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF55705C)
                )
            }
        }
    }
}

@Composable
private fun InlineInfoCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F0))
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB3261E)
        )
    }
}
