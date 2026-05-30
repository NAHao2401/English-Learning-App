package com.example.englishlearningapp.features.lesson.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.foundation.BorderStroke as M3BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.remote.api.response.LessonResponse

private data class LessonVisual(
    val accent: Color,
    val softAccent: Color
)

private data class LockedLessonColors(
    val container: Color,
    val strip: Color,
    val iconBackground: Color,
    val iconContent: Color,
    val title: Color,
    val body: Color,
    val muted: Color,
    val pillBackground: Color,
    val pillContent: Color,
    val progress: Color,
    val progressTrack: Color,
    val orderBackground: Color,
    val orderContent: Color,
    val border: Color,
    val buttonContainer: Color,
    val contentAlpha: Float,
    val buttonBorder: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    lessons: List<LessonResponse>,
    isLoading: Boolean,
    errorMessage: String?,
    onLessonClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lessons",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Continue your learning journey",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE9E7FF))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
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
                isLoading && lessons.isEmpty() -> LoadingContent()

                errorMessage != null && lessons.isEmpty() -> {
                    ErrorContent(message = errorMessage)
                }

                lessons.isEmpty() -> EmptyContent()

                else -> {
                    LessonListContent(
                        lessons = lessons,
                        errorMessage = errorMessage,
                        onLessonClick = onLessonClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonListContent(
    lessons: List<LessonResponse>,
    errorMessage: String?,
    onLessonClick: (Int) -> Unit
) {
    val completedCount = lessons.count { it.status.equals("completed", ignoreCase = true) }
    val unlockedCount = lessons.count { !it.is_locked }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 120.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LessonHeroCard(
                totalLessons = lessons.size,
                completedLessons = completedCount,
                unlockedLessons = unlockedCount
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

        itemsIndexed(lessons) { index, lesson ->
            LessonCard(
                lesson = lesson,
                index = index,
                visual = lessonVisualFor(index),
                onClick = { onLessonClick(lesson.id) }
            )
        }
    }
}

@Composable
private fun LessonHeroCard(
    totalLessons: Int,
    completedLessons: Int,
    unlockedLessons: Int
) {
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
                    .size(118.dp)
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

            Column(
                modifier = Modifier.fillMaxWidth(0.86f)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
                ) {
                    Text(
                        text = "$totalLessons lessons in this topic",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Build your skill step by step",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Complete lessons in order, unlock new content, and track your progress as you learn.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.88f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HeroStatChip(
                        label = "Completed",
                        value = completedLessons.toString()
                    )
                    HeroStatChip(
                        label = "Unlocked",
                        value = unlockedLessons.toString()
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroStatChip(
    label: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.24f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun SectionHeader() {
    Column {
        Text(
            text = "Lesson roadmap",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choose a lesson and continue where you left off",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF7B778C)
        )
    }
}

@Composable
private fun LessonCard(
    lesson: LessonResponse,
    index: Int,
    visual: LessonVisual,
    onClick: () -> Unit
) {
    val enabled = !lesson.is_locked
    if (!enabled) {
        LockedLessonCard(
            lesson = lesson,
            index = index
        )
        return
    }

    val progress = (lesson.completion_percent.coerceIn(0, 100)) / 100f
    val visibleProgress = if (enabled) progress else 0f
    val statusLabel = lessonStatusLabel(lesson.status, lesson.is_locked)
    val actionLabel = lessonActionLabel(lesson)
    val difficulty = lesson.difficulty ?: "Beginner"
    val estimatedTime = lesson.estimated_time?.let { "$it min" } ?: "5 min"
    val lockedColors = lockedLessonColors()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (enabled) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = lockedColors.border,
                        shape = RoundedCornerShape(28.dp)
                    )
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface else lockedColors.container
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(188.dp)
                    .align(Alignment.CenterStart)
                    .background(if (enabled) visual.accent else lockedColors.strip)
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
                            .background(if (enabled) visual.softAccent else lockedColors.iconBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (lesson.is_locked) Icons.Rounded.Lock else Icons.Rounded.MenuBook,
                            contentDescription = lesson.title,
                            tint = if (enabled) visual.accent else lockedColors.iconContent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Box(modifier = Modifier.weight(1f)) {
                    LockedLessonContent(
                        enabled = enabled,
                        mutedColor = lockedColors.muted,
                        alpha = lockedColors.contentAlpha
                    ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = lesson.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (enabled) Color(0xFF242235) else lockedColors.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (enabled) Color(0xFFF4F2FF) else lockedColors.orderBackground
                                    ) {
                                        Text(
                                            text = "#${lesson.lesson_order ?: (index + 1)}",
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (enabled) Color(0xFF6C63FF) else lockedColors.orderContent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(7.dp))

                                Text(
                                    text = lesson.description?.takeIf { it.isNotBlank() }
                                        ?: "Improve your English through this focused lesson.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (enabled) Color(0xFF6E6A7D) else lockedColors.body,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                LockedLessonContent(
                    enabled = enabled,
                    mutedColor = lockedColors.muted,
                    alpha = lockedColors.contentAlpha
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LessonMetaPill(
                                icon = Icons.Rounded.Timelapse,
                                text = difficulty,
                                background = if (enabled) visual.softAccent else lockedColors.pillBackground,
                                content = if (enabled) visual.accent else lockedColors.pillContent
                            )

                            LessonMetaPill(
                                icon = Icons.Rounded.AccessTime,
                                text = estimatedTime,
                                background = if (enabled) Color(0xFFF5F4FA) else lockedColors.pillBackground,
                                content = if (enabled) Color(0xFF6E6A7D) else lockedColors.pillContent
                            )

                            LessonMetaPill(
                                icon = statusIconFor(lesson.status, lesson.is_locked),
                                text = statusLabel,
                                background = statusBackgroundFor(lesson.status, lesson.is_locked, lockedColors),
                                content = statusContentFor(lesson.status, lesson.is_locked, lockedColors)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Progress",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (enabled) Color(0xFF666274) else lockedColors.muted,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = { visibleProgress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = if (enabled) visual.accent else lockedColors.progress,
                                trackColor = if (enabled) Color(0xFFF0EEF6) else lockedColors.progressTrack
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "${lesson.completion_percent}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (enabled) Color(0xFF4E4A5C) else lockedColors.title,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        LockedLessonContent(
                            enabled = enabled,
                            mutedColor = lockedColors.muted,
                            alpha = lockedColors.contentAlpha
                        ) {
                            Text(
                                text = when {
                                    lesson.is_locked -> "Complete previous lesson to unlock"
                                    lesson.status.equals("completed", true) -> "Lesson completed successfully"
                                    lesson.completion_percent > 0 -> "You can continue from your last attempt"
                                    else -> "Ready to begin this lesson"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (enabled) Color(0xFF7B778C) else lockedColors.muted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (enabled) {
                        Button(
                            onClick = onClick,
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = visual.accent,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = actionLabel,
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
                    } else {
                        OutlinedButton(
                            onClick = {},
                            enabled = true,
                            shape = RoundedCornerShape(16.dp),
                            border = M3BorderStroke(1.dp, lockedColors.buttonBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = lockedColors.buttonContainer,
                                contentColor = lockedColors.iconContent
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Locked",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedLessonCard(
    lesson: LessonResponse,
    index: Int
) {
    val lockedColors = lockedLessonColors()
    val difficulty = lesson.difficulty ?: "Beginner"
    val estimatedTime = lesson.estimated_time?.let { "$it min" } ?: "5 min"
    val lockedSurface = lockedColors.container
    val lockIconBackground = if (isSystemInDarkTheme()) lockedSurface else Color(0xFFB7B9C7)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = lockedColors.border,
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = lockedSurface)
    ) {
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
                        .background(lockIconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = lesson.title,
                        tint = lockedColors.iconContent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Box(modifier = Modifier.weight(1f)) {
                    LockedLessonContent(
                        enabled = false,
                        mutedColor = lockedColors.muted,
                        alpha = lockedColors.contentAlpha
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = lockedColors.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = lockedSurface
                                ) {
                                    Text(
                                        text = "#${lesson.lesson_order ?: (index + 1)}",
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = lockedColors.orderContent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(7.dp))

                            Text(
                                text = lesson.description?.takeIf { it.isNotBlank() }
                                    ?: "Improve your English through this focused lesson.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = lockedColors.body,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            LockedLessonContent(
                enabled = false,
                mutedColor = lockedColors.muted,
                alpha = lockedColors.contentAlpha
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LessonMetaPill(
                            icon = Icons.Rounded.Timelapse,
                            text = difficulty,
                            background = lockedColors.pillBackground,
                            content = lockedColors.pillContent,
                            border = lockedColors.buttonBorder
                        )

                        LessonMetaPill(
                            icon = Icons.Rounded.AccessTime,
                            text = estimatedTime,
                            background = lockedColors.pillBackground,
                            content = lockedColors.pillContent,
                            border = lockedColors.buttonBorder
                        )

                        LessonMetaPill(
                            icon = Icons.Rounded.Lock,
                            text = "Locked",
                            background = lockedColors.pillBackground,
                            content = lockedColors.pillContent,
                            border = lockedColors.buttonBorder
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = lockedColors.muted,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(lockedColors.progressTrack)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF9497A3),
                                    shape = RoundedCornerShape(50)
                                )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "${lesson.completion_percent}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = lockedColors.title,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    LockedLessonContent(
                        enabled = false,
                        mutedColor = lockedColors.muted,
                        alpha = lockedColors.contentAlpha
                    ) {
                        Text(
                            text = "Complete previous lesson to unlock",
                            style = MaterialTheme.typography.bodySmall,
                            color = lockedColors.muted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = {},
                    enabled = true,
                    shape = RoundedCornerShape(16.dp),
                    border = M3BorderStroke(1.dp, lockedColors.buttonBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = lockedColors.buttonContainer,
                        contentColor = lockedColors.iconContent
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Locked",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LockedLessonContent(
    enabled: Boolean,
    mutedColor: Color,
    alpha: Float,
    content: @Composable () -> Unit
) {
    if (enabled) {
        content()
    } else {
        CompositionLocalProvider(
            LocalContentColor provides mutedColor.copy(alpha = alpha)
        ) {
            Box(modifier = Modifier.alpha(alpha)) {
                content()
            }
        }
    }
}

@Composable
private fun lockedLessonColors(): LockedLessonColors {
    return if (isSystemInDarkTheme()) {
        LockedLessonColors(
            container = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
            strip = Color(0xFFD8D5E5),
            iconBackground = Color(0xFFEAE8F1),
            iconContent = Color(0xFF8F8A9D),
            title = Color(0xFF6E6A7D),
            body = Color(0xFF6E6A7D),
            muted = Color(0xFF7B778C),
            pillBackground = Color(0xFFEAE8F1),
            pillContent = Color(0xFF8F8A9D),
            progress = Color(0xFFBDB8CC),
            progressTrack = Color(0xFFF0EEF6),
            orderBackground = Color(0xFFF4F2FF),
            orderContent = Color(0xFF6C63FF),
            border = Color.Gray.copy(alpha = 0.2f),
            buttonContainer = MaterialTheme.colorScheme.surface,
            contentAlpha = 0.42f,
            buttonBorder = Color(0xFFD4D0E0)
        )
    } else {
        val lockedSurface = Color(0xFFF0F0F3)
        LockedLessonColors(
            container = lockedSurface,
            strip = lockedSurface,
            iconBackground = Color(0xFFFAFAFC),
            iconContent = Color(0xFF4D4F60),
            title = Color(0xFF34353D),
            body = Color(0xFF50515B),
            muted = Color(0xFF5B5D68),
            pillBackground = Color(0xFFDCDDE4),
            pillContent = Color(0xFF50525E),
            progress = Color.Transparent,
            progressTrack = Color(0xFFBFC1CB),
            orderBackground = lockedSurface,
            orderContent = Color(0xFF666666),
            border = Color(0xFFD6D6DA),
            buttonContainer = Color(0xFFE8E8EC),
            contentAlpha = 0.78f,
            buttonBorder = Color(0xFFBFC0C8)
        )
    }
}

@Composable
private fun LessonMetaPill(
    icon: ImageVector,
    text: String,
    background: Color,
    content: Color,
    border: Color? = null
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = background,
        border = border?.let { BorderStroke(1.dp, it) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(14.dp),
                tint = content
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = content,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
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
                text = "Loading lessons...",
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
                    text = "No lessons yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF242235)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lessons for this topic will appear here once they are available.",
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

private fun lessonVisualFor(index: Int): LessonVisual {
    val palette = listOf(
        LessonVisual(
            accent = Color(0xFFFF7A59),
            softAccent = Color(0xFFFFE5DD)
        ),
        LessonVisual(
            accent = Color(0xFF3A86FF),
            softAccent = Color(0xFFE0ECFF)
        ),
        LessonVisual(
            accent = Color(0xFF7B61FF),
            softAccent = Color(0xFFEAE5FF)
        ),
        LessonVisual(
            accent = Color(0xFF00A878),
            softAccent = Color(0xFFDDF7EF)
        )
    )

    return palette[index % palette.size]
}

private fun lessonStatusLabel(status: String, isLocked: Boolean): String {
    if (isLocked) return "Locked"

    return when (status.lowercase()) {
        "completed" -> "Completed"
        "in_progress" -> "In progress"
        "not_started" -> "Not started"
        else -> "Not started"
    }
}

private fun lessonActionLabel(lesson: LessonResponse): String {
    return when {
        lesson.is_locked -> "Locked"
        lesson.status.equals("completed", true) -> "Review"
        lesson.completion_percent > 0 -> "Continue"
        else -> "Start"
    }
}

private fun statusIconFor(status: String, isLocked: Boolean): ImageVector {
    if (isLocked) return Icons.Rounded.Lock

    return when (status.lowercase()) {
        "completed" -> Icons.Rounded.CheckCircle
        "in_progress" -> Icons.Rounded.PlayArrow
        else -> Icons.Rounded.MenuBook
    }
}

private fun statusBackgroundFor(
    status: String,
    isLocked: Boolean,
    lockedColors: LockedLessonColors
): Color {
    if (isLocked) return lockedColors.pillBackground

    return when (status.lowercase()) {
        "completed" -> Color(0xFFDDF7EF)
        "in_progress" -> Color(0xFFEAE5FF)
        else -> Color(0xFFF5F4FA)
    }
}

private fun statusContentFor(
    status: String,
    isLocked: Boolean,
    lockedColors: LockedLessonColors
): Color {
    if (isLocked) return lockedColors.pillContent

    return when (status.lowercase()) {
        "completed" -> Color(0xFF00A878)
        "in_progress" -> Color(0xFF7B61FF)
        else -> Color(0xFF6E6A7D)
    }
}
