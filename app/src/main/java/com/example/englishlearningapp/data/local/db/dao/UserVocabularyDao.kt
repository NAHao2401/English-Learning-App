package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.UserVocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserVocabularyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserVocabulary(userVocabulary: UserVocabularyEntity): Long

    @Update
    suspend fun updateUserVocabulary(userVocabulary: UserVocabularyEntity)

    @Delete
    suspend fun deleteUserVocabulary(userVocabulary: UserVocabularyEntity)

    @Query("""
        SELECT * FROM user_vocabularies
        WHERE user_id = :userId
        ORDER BY last_reviewed_at DESC
    """)
    fun getUserVocabularies(userId: Int): Flow<List<UserVocabularyEntity>>

    @Query("""
        SELECT * FROM user_vocabularies
        WHERE user_id = :userId AND is_saved = 1
        ORDER BY last_reviewed_at DESC
    """)
    fun getSavedUserVocabularies(userId: Int): Flow<List<UserVocabularyEntity>>

    @Query("""
        SELECT * FROM user_vocabularies
        WHERE user_id = :userId AND vocabulary_id = :vocabularyId
        LIMIT 1
    """)
    suspend fun getUserVocabulary(userId: Int, vocabularyId: Int): UserVocabularyEntity?

    @Query("""
        UPDATE user_vocabularies
        SET is_saved = :isSaved
        WHERE user_id = :userId AND vocabulary_id = :vocabularyId
    """)
    suspend fun updateSavedState(userId: Int, vocabularyId: Int, isSaved: Boolean)

    @Query("""
        UPDATE user_vocabularies
        SET mastery_level = :masteryLevel,
            review_count = :reviewCount,
            last_reviewed_at = :lastReviewedAt
        WHERE user_id = :userId AND vocabulary_id = :vocabularyId
    """)
    suspend fun updateReviewInfo(
        userId: Int,
        vocabularyId: Int,
        masteryLevel: Int,
        reviewCount: Int,
        lastReviewedAt: Long
    )
}