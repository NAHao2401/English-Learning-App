package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.AnswerOptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerOptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerOption(answerOption: AnswerOptionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerOptions(answerOptions: List<AnswerOptionEntity>)

    @Update
    suspend fun updateAnswerOption(answerOption: AnswerOptionEntity)

    @Delete
    suspend fun deleteAnswerOption(answerOption: AnswerOptionEntity)

    @Query("SELECT * FROM answer_options WHERE question_id = :questionId ORDER BY option_order ASC")
    fun getOptionsByQuestion(questionId: Int): Flow<List<AnswerOptionEntity>>
}