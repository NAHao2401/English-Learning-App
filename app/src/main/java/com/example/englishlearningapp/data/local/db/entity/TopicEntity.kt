package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "topics",
    indices = [
        Index(value = ["name"])
    ]
)
data class TopicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val description: String? = null,

    @ColumnInfo(name = "icon_url")
    val iconUrl: String? = null,

    val level: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null
)