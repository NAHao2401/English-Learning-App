package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "speaking_practices",
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
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["lesson_id"])
    ]
)
data class SpeakingPracticeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "lesson_id")
    val lessonId: Int? = null,

    @ColumnInfo(name = "target_text")
    val targetText: String,

    @ColumnInfo(name = "spoken_text")
    val spokenText: String? = null,

    @ColumnInfo(name = "is_matched")
    val isMatched: Boolean = false,

    val score: Int? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null
)