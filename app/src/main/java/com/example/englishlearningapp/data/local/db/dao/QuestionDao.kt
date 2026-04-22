package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    @Query("SELECT * FROM questions WHERE id = :questionId LIMIT 1")
    suspend fun getQuestionById(questionId: Int): QuestionEntity?

    @Query("SELECT * FROM questions WHERE lesson_id = :lessonId ORDER BY question_order ASC")
    fun getQuestionsByLesson(lessonId: Int): Flow<List<QuestionEntity>>
}