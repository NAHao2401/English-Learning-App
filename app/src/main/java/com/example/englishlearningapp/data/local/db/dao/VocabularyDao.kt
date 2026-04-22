package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(vocabulary: VocabularyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabularies(vocabularies: List<VocabularyEntity>)

    @Update
    suspend fun updateVocabulary(vocabulary: VocabularyEntity)

    @Delete
    suspend fun deleteVocabulary(vocabulary: VocabularyEntity)

    @Query("SELECT * FROM vocabularies ORDER BY word ASC")
    fun getAllVocabularies(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabularies WHERE id = :vocabularyId LIMIT 1")
    suspend fun getVocabularyById(vocabularyId: Int): VocabularyEntity?

    @Query("SELECT * FROM vocabularies WHERE topic_id = :topicId ORDER BY word ASC")
    fun getVocabulariesByTopic(topicId: Int): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabularies WHERE word LIKE '%' || :keyword || '%' ORDER BY word ASC")
    fun searchVocabulary(keyword: String): Flow<List<VocabularyEntity>>
}