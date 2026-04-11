package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "answer_options",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["question_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["question_id"])
    ]
)
data class AnswerOptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "question_id")
    val questionId: Int,

    @ColumnInfo(name = "option_text")
    val optionText: String,

    @ColumnInfo(name = "is_correct")
    val isCorrect: Boolean = false,

    @ColumnInfo(name = "option_order")
    val optionOrder: Int? = null
)