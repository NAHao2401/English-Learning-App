package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class TopicWithCount(
    @Embedded val topic: TopicEntity,
    @ColumnInfo(name = "wordCount") val wordCount: Int
)
