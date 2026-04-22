package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabularies",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["topic_id"]),
        Index(value = ["word"])
    ]
)
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "topic_id")
    val topicId: Int,

    val word: String,

    val meaning: String,

    val pronunciation: String? = null,

    @ColumnInfo(name = "example_sentence")
    val exampleSentence: String? = null,

    @ColumnInfo(name = "audio_url")
    val audioUrl: String? = null,

    val difficulty: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null
)