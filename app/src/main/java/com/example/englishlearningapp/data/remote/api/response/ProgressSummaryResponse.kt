package com.example.englishlearningapp.data.remote.api.response

data class ProgressSummaryResponse(
    val total_xp: Int,
    val streak_count: Int,
    val current_level: String,
    val completed_lessons: Int,
    val total_lessons: Int,
    val completion_percent: Int,
    val study_days: Int,

    val in_progress_lessons: Int = 0,
    val not_started_lessons: Int = 0,
    val locked_lessons: Int = 0,

    val total_submissions: Int = 0,
    val average_score: Int = 0,
    val best_score: Int = 0,

    val remaining_lessons: Int = 0,
    val lessons_chart: LessonStatusDistributionResponse? = null,
    val weekly_xp: List<DailyXpResponse> = emptyList(),
    val level_progress: LevelProgressResponse? = null,
    val recent_activities: List<RecentActivityResponse> = emptyList()
)

data class DailyXpResponse(
    val date: String,
    val xp: Int,
    val completed_lessons: Int
)

data class LessonStatusDistributionResponse(
    val completed: Int,
    val in_progress: Int,
    val not_started: Int,
    val locked: Int
)

data class LevelProgressResponse(
    val current_level: String,
    val current_xp: Int,
    val current_level_min_xp: Int,
    val next_level: String?,
    val next_level_min_xp: Int?,
    val progress_percent: Int
)

data class RecentActivityResponse(
    val lesson_id: Int,
    val lesson_title: String,
    val score: Int,
    val xp_earned: Int,
    val submitted_at: String
)