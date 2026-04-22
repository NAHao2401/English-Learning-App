package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity): Long

    @Update
    suspend fun updateProgress(progress: ProgressEntity)

    @Delete
    suspend fun deleteProgress(progress: ProgressEntity)

    @Query("""
        SELECT * FROM progresses
        WHERE user_id = :userId
        ORDER BY last_accessed_at DESC
    """)
    fun getProgressByUser(userId: Int): Flow<List<ProgressEntity>>

    @Query("""
        SELECT * FROM progresses
        WHERE user_id = :userId AND lesson_id = :lessonId
        LIMIT 1
    """)
    suspend fun getProgressForLesson(userId: Int, lessonId: Int): ProgressEntity?

    @Query("""
        UPDATE progresses
        SET status = :status,
            completion_percent = :completionPercent,
            highest_score = :highestScore,
            last_accessed_at = :lastAccessedAt,
            completed_at = :completedAt
        WHERE user_id = :userId AND lesson_id = :lessonId
    """)
    suspend fun updateLessonProgress(
        userId: Int,
        lessonId: Int,
        status: String,
        completionPercent: Int,
        highestScore: Int,
        lastAccessedAt: Long?,
        completedAt: Long?
    )

    @Query("""
        SELECT * FROM progresses
        WHERE user_id = :userId AND status = 'completed'
        ORDER BY completed_at DESC
    """)
    fun getCompletedLessons(userId: Int): Flow<List<ProgressEntity>>
}