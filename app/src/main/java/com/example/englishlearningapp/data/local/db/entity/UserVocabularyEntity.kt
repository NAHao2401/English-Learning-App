package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_vocabularies",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = VocabularyEntity::class,
            parentColumns = ["id"],
            childColumns = ["vocabulary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["vocabulary_id"]),
        Index(value = ["user_id", "vocabulary_id"], unique = true)
    ]
)
data class UserVocabularyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "vocabulary_id")
    val vocabularyId: Int,

    @ColumnInfo(name = "is_saved")
    val isSaved: Boolean = false,

    @ColumnInfo(name = "mastery_level")
    val masteryLevel: Int = 0,

    @ColumnInfo(name = "last_reviewed_at")
    val lastReviewedAt: Long? = null,

    @ColumnInfo(name = "review_count")
    val reviewCount: Int = 0
)