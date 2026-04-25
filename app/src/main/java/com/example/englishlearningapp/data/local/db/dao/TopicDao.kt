package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.TopicEntity
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Delete
    suspend fun deleteTopic(topic: TopicEntity)

    @Query("SELECT * FROM topics ORDER BY name ASC")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :topicId LIMIT 1")
    suspend fun getTopicById(topicId: Int): TopicEntity?

    @Query("SELECT * FROM topics WHERE name LIKE '%' || :keyword || '%' ORDER BY name ASC")
    fun searchTopics(keyword: String): Flow<List<TopicEntity>>

    @Query("SELECT COUNT(*) FROM topics WHERE name = :name AND level = :level")
    suspend fun getTopicCountByNameAndLevelSync(name: String, level: String): Int

    @Query(
        """
        SELECT t.*, COUNT(v.id) as wordCount FROM topics t
        LEFT JOIN vocabularies v ON t.id = v.topic_id
        GROUP BY t.id
        ORDER BY t.name ASC
        """
    )
    fun getTopicsWithWordCount(): Flow<List<TopicWithCount>>
}