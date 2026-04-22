package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["lesson_id"])
    ]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,

    @ColumnInfo(name = "question_type")
    val questionType: String,

    @ColumnInfo(name = "question_text")
    val questionText: String,

    @ColumnInfo(name = "audio_url")
    val audioUrl: String? = null,

    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String? = null,

    val explanation: String? = null,

    @ColumnInfo(name = "question_order")
    val questionOrder: Int? = null
)