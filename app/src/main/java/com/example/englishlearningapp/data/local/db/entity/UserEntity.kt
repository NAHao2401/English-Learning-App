package com.example.englishlearningapp.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val email: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo(name = "current_level")
    val currentLevel: String = "Beginner",

    @ColumnInfo(name = "total_xp")
    val totalXp: Int = 0,

    @ColumnInfo(name = "streak_count")
    val streakCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null
)