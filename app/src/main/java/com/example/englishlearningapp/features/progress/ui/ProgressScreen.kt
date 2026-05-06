package com.example.englishlearningapp.features.progress.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.remote.api.response.ProgressSummaryResponse
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    summary: ProgressSummaryResponse?,
    isLoading: Boolean,
    errorMessage: String?,
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
                            text = "Progress",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B2F)
                        )
                        Text(
                            text = "Track your learning journey",
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
                isLoading && summary == null -> LoadingContent()
                errorMessage != null && summary == null -> ErrorContent(message = errorMessage)
                summary == null -> EmptyProgressContent()
                else -> ProgressContent(summary = summary, errorMessage = errorMessage)
            }
        }
    }
}

@Composable
private fun ProgressContent(
    summary: ProgressSummaryResponse,
    errorMessage: String?
) {
    val completionPercent = summary.completion_percent.coerceIn(0, 100)
    val progress = completionPercent / 100f

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
            ProgressHeroCard(
                level = summary.current_level,
                totalXp = summary.total_xp,
                completionPercent = completionPercent,
                progress = progress
            )
        }

        if (errorMessage != null) {
            item {
                InlineErrorCard(message = errorMessage)
            }
        }

        item {
            SectionHeader()
        }

        item {
            StatsGrid(summary = summary)
        }

        item {
            ProgressChartCard(summary = summary)
        }

        item {
            LearningProgressCard(summary = summary, progress = progress)
        }

        item {
            MotivationCard(summary = summary)
        }
    }
}

@Composable
private fun ProgressHeroCard(
    level: String,
    totalXp: Int,
    completionPercent: Int,
    progress: Float
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
                    .size(122.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
            )

            Column(
                modifier = Modifier.fillMaxWidth(0.88f)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "You're on a great track",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Level $level • $totalXp XP earned",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.90f),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(18.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(50)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$completionPercent% overall completion",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionHeader() {
    Column {
        Text(
            text = "Learning overview",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF1D1B2F),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your study stats and course completion",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF7B778C)
        )
    }
}

@Composable
private fun StatsGrid(summary: ProgressSummaryResponse) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProgressStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.TrendingUp,
                label = "Total XP",
                value = summary.total_xp.toString(),
                background = Color(0xFFEAE5FF),
                content = Color(0xFF7B61FF)
            )

            ProgressStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.LocalFireDepartment,
                label = "Streak",
                value = "${summary.streak_count} days",
                background = Color(0xFFFFF0CC),
                content = Color(0xFFFFB020)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProgressStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.School,
                label = "Study days",
                value = summary.study_days.toString(),
                background = Color(0xFFE0ECFF),
                content = Color(0xFF3A86FF)
            )

            ProgressStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.MenuBook,
                label = "Completed",
                value = "${summary.completed_lessons}/${summary.total_lessons}",
                background = Color(0xFFDDF7EF),
                content = Color(0xFF00A878)
            )
        }
    }
}

@Composable
private fun ProgressStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    background: Color,
    content: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = background
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF242235),
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF6E6A7D),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LearningProgressCard(
    summary: ProgressSummaryResponse,
    progress: Float
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE0ECFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timeline,
                        contentDescription = null,
                        tint = Color(0xFF3A86FF)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Course completion",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF242235),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${summary.completed_lessons} of ${summary.total_lessons} lessons completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7B778C)
                    )
                }

                Text(
                    text = "${summary.completion_percent.coerceIn(0, 100)}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF3A86FF),
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF3A86FF),
                trackColor = Color(0xFFE0ECFF)
            )
        }
    }
}

@Composable
private fun MotivationCard(summary: ProgressSummaryResponse) {
    val message = when {
        summary.streak_count >= 7 -> "Amazing consistency! Keep your weekly streak alive and continue building your English habit."
        summary.streak_count >= 2 -> "Nice streak! Study again tomorrow to keep the momentum going."
        summary.completed_lessons > 0 -> "Good start! Complete another lesson to grow your XP and progress faster."
        else -> "Start your first lesson today and begin building your English learning streak."
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFF0CC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB020)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Today's motivation",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF242235),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6E6A7D)
            )
        }
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
                text = "Loading progress...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6E6A7D)
            )
        }
    }
}

@Composable
private fun EmptyProgressContent() {
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
                    text = "No progress yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF242235),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Complete your first lesson to see your progress here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6E6A7D),
                    textAlign = TextAlign.Center
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFC7C2)),
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

@Composable
private fun ProgressChartCard(
    summary: ProgressSummaryResponse
) {
    val completed = summary.completed_lessons
    val total = summary.total_lessons.coerceAtLeast(1)
    val remaining = (total - completed).coerceAtLeast(0)
    val progress = completed.toFloat() / total.toFloat()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Completion chart",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF242235),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Visual overview of your lesson progress",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF7B778C)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DonutChart(
                    progress = progress,
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ChartLegendItem(
                        color = Color(0xFF6C63FF),
                        label = "Completed",
                        value = "$completed lessons"
                    )

                    ChartLegendItem(
                        color = Color(0xFFE6E3F3),
                        label = "Remaining",
                        value = "$remaining lessons"
                    )

                    ChartLegendItem(
                        color = Color(0xFF3A86FF),
                        label = "Completion",
                        value = "${summary.completion_percent.coerceIn(0, 100)}%"
                    )
                }
            }
        }
    }
}

@Composable
private fun DonutChart(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 26.dp.toPx()
            val size = Size(size.width, size.height)
            val topLeft = Offset(0f, 0f)

            drawArc(
                color = Color(0xFFEAE5FF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = Color(0xFF6C63FF),
                startAngle = -90f,
                sweepAngle = 360f * clampedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(clampedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF242235),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Completed",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF7B778C)
            )
        }
    }
}

@Composable
private fun ChartLegendItem(
    color: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF7B778C),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF242235),
                fontWeight = FontWeight.Bold
            )
        }
    }
}