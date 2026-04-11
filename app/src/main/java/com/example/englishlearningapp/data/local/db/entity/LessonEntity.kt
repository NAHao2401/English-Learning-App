package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["topic_id"])
    ]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "topic_id")
    val topicId: Int,

    val title: String,

    val description: String? = null,

    @ColumnInfo(name = "lesson_order")
    val lessonOrder: Int? = null,

    val difficulty: String? = null,

    @ColumnInfo(name = "estimated_time")
    val estimatedTime: Int? = null,

    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null
)