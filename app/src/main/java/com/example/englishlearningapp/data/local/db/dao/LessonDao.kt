package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity)

    @Query("SELECT * FROM lessons ORDER BY lesson_order ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: Int): LessonEntity?

    @Query("SELECT * FROM lessons WHERE topic_id = :topicId ORDER BY lesson_order ASC")
    fun getLessonsByTopic(topicId: Int): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE topic_id = :topicId AND is_locked = 0 ORDER BY lesson_order ASC")
    fun getUnlockedLessonsByTopic(topicId: Int): Flow<List<LessonEntity>>
}