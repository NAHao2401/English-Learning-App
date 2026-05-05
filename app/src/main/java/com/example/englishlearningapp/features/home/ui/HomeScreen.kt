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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class HomeFeatureItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    userName: String = "Learner",
    totalXp: Int = 120,
    streakCount: Int = 5,
    completedLessons: Int = 8,
    onLessonsClick: () -> Unit = {},
    onVocabularyClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    onAiScanClick: () -> Unit = {},
    onSpeakingClick: () -> Unit = {},
    onContinueLearningClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val featureItems = listOf(
        HomeFeatureItem("Lessons", Icons.Default.AutoStories, onLessonsClick),
        HomeFeatureItem("Vocabulary", Icons.Default.Translate, onVocabularyClick),
        HomeFeatureItem("Progress", Icons.Default.BarChart, onProgressClick),
        HomeFeatureItem("AI Scan", Icons.Default.CameraAlt, onAiScanClick),
        HomeFeatureItem("Speaking", Icons.Default.Mic, onSpeakingClick)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    HomeHeader(
                        userName = userName,
                        onLogoutClick = onLogoutClick
                    )
                }

                item {
                    StatsSection(
                        totalXp = totalXp,
                        streakCount = streakCount,
                        completedLessons = completedLessons
                    )
                }

                item {
                    ContinueLearningCard(
                        onContinueLearningClick = onContinueLearningClick
                    )
                }

                item {
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        featureItems.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                when (rowItems.size) {
                                    2 -> {
                                        FeatureCard(
                                            item = rowItems[0],
                                            modifier = Modifier.weight(1f)
                                        )
                                        FeatureCard(
                                            item = rowItems[1],
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    1 -> {
                                        Spacer(modifier = Modifier.weight(0.5f))
                                        FeatureCard(
                                            item = rowItems[0],
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.weight(0.5f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DailyMotivationCard()
            }
        }
    }
}

@Composable
fun HomeHeader(
    userName: String,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome back, $userName 👋",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onLogoutClick) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Let’s continue your English learning journey today.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun StatsSection(
    totalXp: Int,
    streakCount: Int,
    completedLessons: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "XP",
            value = totalXp.toString()
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Streak",
            value = "$streakCount days"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Lessons",
            value = completedLessons.toString()
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ContinueLearningCard(
    onContinueLearningClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Continue Learning",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Resume your current lesson and keep your streak alive.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onContinueLearningClick) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun FeatureCard(
    item: HomeFeatureItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(132.dp)
            .clickable { item.onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun DailyMotivationCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Motivation"
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Daily Motivation",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Practice a little every day, and your progress will grow faster.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}