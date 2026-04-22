package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "progresses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["lesson_id"]),
        Index(value = ["user_id", "lesson_id"], unique = true)
    ]
)
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,

    val status: String = "not_started",

    @ColumnInfo(name = "completion_percent")
    val completionPercent: Int = 0,

    @ColumnInfo(name = "highest_score")
    val highestScore: Int = 0,

    @ColumnInfo(name = "last_accessed_at")
    val lastAccessedAt: Long? = null,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null
)