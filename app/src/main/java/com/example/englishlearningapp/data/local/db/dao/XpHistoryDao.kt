package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.XpHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface XpHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertXpHistory(xpHistory: XpHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertXpHistories(histories: List<XpHistoryEntity>)

    @Delete
    suspend fun deleteXpHistory(xpHistory: XpHistoryEntity)

    @Query("""
        SELECT * FROM xp_histories
        WHERE user_id = :userId
        ORDER BY created_at DESC
    """)
    fun getXpHistoriesByUser(userId: Int): Flow<List<XpHistoryEntity>>

    @Query("""
        SELECT COALESCE(SUM(xp_amount), 0)
        FROM xp_histories
        WHERE user_id = :userId
    """)
    suspend fun getTotalXpByUser(userId: Int): Int
}