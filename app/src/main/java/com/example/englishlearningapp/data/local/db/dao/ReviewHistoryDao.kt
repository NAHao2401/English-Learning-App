package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.ReviewHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewHistory(reviewHistory: ReviewHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewHistories(reviewHistories: List<ReviewHistoryEntity>)

    @Delete
    suspend fun deleteReviewHistory(reviewHistory: ReviewHistoryEntity)

    @Query("""
        SELECT * FROM review_histories
        WHERE user_id = :userId
        ORDER BY reviewed_at DESC
    """)
    fun getReviewHistoriesByUser(userId: Int): Flow<List<ReviewHistoryEntity>>

    @Query("""
        SELECT * FROM review_histories
        WHERE user_id = :userId AND vocabulary_id = :vocabularyId
        ORDER BY reviewed_at DESC
    """)
    fun getReviewHistoriesByVocabulary(userId: Int, vocabularyId: Int): Flow<List<ReviewHistoryEntity>>
}