package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

data class DifficultyCount(
    val difficulty: String,
    val count: Int
)

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

    @Query("SELECT * FROM vocabularies WHERE topic_id = :topicId AND (:difficulty IS NULL OR difficulty = :difficulty) ORDER BY word ASC")
    fun getVocabulariesByTopicFiltered(topicId: Int, difficulty: String?): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabularies WHERE word LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%' ORDER BY word ASC LIMIT 30")
    fun searchVocabularies(query: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabularies WHERE id IN (:ids)")
    fun getVocabulariesByIds(ids: List<Int>): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabularies WHERE word LIKE '%' || :keyword || '%' ORDER BY word ASC")
    fun searchVocabulary(keyword: String): Flow<List<VocabularyEntity>>

    @Query(
        """
        SELECT difficulty, COUNT(*) as count
        FROM vocabularies
        WHERE difficulty IS NOT NULL
        GROUP BY difficulty
        """
    )
    fun getVocabCountByDifficulty(): Flow<List<DifficultyCount>>

    @Query("SELECT COUNT(*) FROM vocabularies WHERE difficulty = :difficulty")
    suspend fun getVocabCountByDifficultySync(difficulty: String): Int
}