package com.example.englishlearningapp.data.local.db

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.englishlearningapp.data.local.db.entity.TopicEntity
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.response.TopicResponse
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

object DatabaseSeeder {

    private data class VocabularySeed(
        val word: String,
        val meaning: String,
        val pronunciation: String,
        val exampleSentence: String
    )

    private data class TopicSeed(
        val name: String,
        val description: String,
        val iconUrl: String,
        val level: String,
        val vocabularies: List<VocabularySeed>
    )

    fun callback(context: Context): RoomDatabase.Callback {
        return object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    RetrofitClient.init(context.applicationContext)
                    seedDatabase(DatabaseProvider.getDatabase(context.applicationContext))
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                CoroutineScope(Dispatchers.IO).launch {
                    // Ensure database is populated if empty at open time
                    RetrofitClient.init(context.applicationContext)
                    seedDatabase(DatabaseProvider.getDatabase(context.applicationContext))
                }
            }
        }
    }

    private suspend fun seedDatabase(database: AppDatabase) {
        // If DB is empty (no topics), fetch topics and vocabularies from backend and insert
        val existingTopics = database.topicDao().getAllTopics()
            .firstOrNull()
        if (existingTopics == null || existingTopics.isEmpty()) {
            try {
                val api = RetrofitClient.vocabApiService
                val topics: List<TopicResponse> = try {
                    api.getTopics()
                } catch (e: Exception) {
                    emptyList()
                }

                topics.forEach { t ->
                    val topicId = database.topicDao().insertTopic(
                        TopicEntity(
                            name = t.name,
                            description = t.description,
                            iconUrl = t.iconEmoji ?: t.color,
                            level = t.level,
                            createdAt = System.currentTimeMillis()
                        )
                    ).toInt()

                    val vocabs: List<VocabularyResponse> = try {
                        api.getVocabulariesByTopic(t.id)
                    } catch (e: Exception) {
                        emptyList()
                    }

                    if (vocabs.isNotEmpty()) {
                        val entities = vocabs.map { v ->
                            VocabularyEntity(
                                id = v.id,
                                topicId = topicId,
                                word = v.word,
                                meaning = v.meaning,
                                pronunciation = v.pronunciation ?: "",
                                exampleSentence = v.exampleSentence,
                                audioUrl = v.audioUrl,
                                difficulty = v.difficulty ?: t.level,
                                createdAt = System.currentTimeMillis()
                            )
                        }
                        database.vocabularyDao().insertVocabularies(entities)
                    }
                }
            } catch (_: Exception) {
                // Ignore seeding errors - app can function without remote seed
            }
        }
    }
    // topicSeeds removed: data will be fetched from backend instead
}