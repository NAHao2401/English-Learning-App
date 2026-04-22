package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scan_extracted_items",
    foreignKeys = [
        ForeignKey(
            entity = ScanSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["scan_session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scan_session_id"])
    ]
)
data class ScanExtractedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "scan_session_id")
    val scanSessionId: Int,

    val word: String,

    val meaning: String? = null,

    val pronunciation: String? = null,

    @ColumnInfo(name = "example_sentence")
    val exampleSentence: String? = null,

    @ColumnInfo(name = "is_saved")
    val isSaved: Boolean = false
)