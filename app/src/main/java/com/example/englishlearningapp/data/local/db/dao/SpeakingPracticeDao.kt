package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.SpeakingPracticeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeakingPracticeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakingPractice(practice: SpeakingPracticeEntity): Long

    @Update
    suspend fun updateSpeakingPractice(practice: SpeakingPracticeEntity)

    @Delete
    suspend fun deleteSpeakingPractice(practice: SpeakingPracticeEntity)

    @Query("""
        SELECT * FROM speaking_practices
        WHERE user_id = :userId
        ORDER BY created_at DESC
    """)
    fun getPracticesByUser(userId: Int): Flow<List<SpeakingPracticeEntity>>

    @Query("""
        SELECT * FROM speaking_practices
        WHERE user_id = :userId AND lesson_id = :lessonId
        ORDER BY created_at DESC
    """)
    fun getPracticesByLesson(userId: Int, lessonId: Int): Flow<List<SpeakingPracticeEntity>>

    @Query("SELECT * FROM speaking_practices WHERE id = :practiceId LIMIT 1")
    suspend fun getPracticeById(practiceId: Int): SpeakingPracticeEntity?
}