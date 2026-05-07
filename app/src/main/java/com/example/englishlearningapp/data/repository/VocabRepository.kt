package com.example.englishlearningapp.data.repository

import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.local.db.dao.TopicDao
import com.example.englishlearningapp.data.local.db.dao.UserVocabularyDao
import com.example.englishlearningapp.data.local.db.dao.VocabularyDao
import com.example.englishlearningapp.data.local.db.entity.TopicEntity
import com.example.englishlearningapp.data.local.db.entity.TopicWithCount
import com.example.englishlearningapp.data.local.db.entity.UserVocabularyEntity
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import com.example.englishlearningapp.data.remote.api.response.VocabularyResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabRepository @Inject constructor(
	private val vocabularyDao: VocabularyDao,
	private val userVocabularyDao: UserVocabularyDao,
	private val topicDao: TopicDao,
) {

	fun getTopics(): Flow<List<TopicEntity>> {
		return topicDao.getAllTopics()
	}

	fun getTopicsWithWordCount(): Flow<List<TopicWithCount>> {
		return topicDao.getTopicsWithWordCount()
	}

	fun getVocabsByTopic(topicId: Int): Flow<List<VocabularyEntity>> {
		return vocabularyDao.getVocabulariesByTopic(topicId)
	}

	fun getVocabsByTopicFiltered(topicId: Int, difficulty: String?): Flow<List<VocabularyEntity>> {
		return vocabularyDao.getVocabulariesByTopicFiltered(topicId, difficulty)
	}

	fun searchVocabs(query: String): Flow<List<VocabularyEntity>> {
		return vocabularyDao.searchVocabularies(query)
	}

	fun getSavedVocabs(userId: Int): Flow<List<VocabularyEntity>> {
		return userVocabularyDao.getSavedVocabularies(userId)
	}

	fun getVocabCountByLevel(): Flow<Map<String, Int>> {
		return vocabularyDao.getVocabCountByDifficulty()
			.map { list -> list.associate { it.difficulty to it.count } }
	}

	fun getVocabsByIds(ids: List<Int>): Flow<List<VocabularyEntity>> {
		return vocabularyDao.getVocabulariesByIds(ids)
	}

	suspend fun getVocabsByLevel(level: String): Result<List<VocabularyEntity>> {
		return try {
			val response = RetrofitClient.vocabApiService.getAllVocabularies(level)
			Result.success(response.map { it.toEntity() })
		} catch (e: Exception) {
			Result.failure(Exception(e.message ?: "Network error"))
		}
	}

	suspend fun toggleSave(userId: Int, vocabId: Int, save: Boolean) {
		val existing = userVocabularyDao.getUserVocabulary(userId, vocabId)
		val entity = if (existing != null) {
			existing.copy(isSaved = save)
		} else {
			UserVocabularyEntity(
				userId = userId,
				vocabularyId = vocabId,
				isSaved = save
			)
		}
		userVocabularyDao.upsertUserVocabulary(entity)
	}

	suspend fun isSaved(userId: Int, vocabId: Int): Boolean {
		return userVocabularyDao.getUserVocabulary(userId, vocabId)?.isSaved ?: false
	}

	private fun VocabularyResponse.toEntity(): VocabularyEntity {
		return VocabularyEntity(
			id = id,
			topicId = topicId,
			word = word,
			meaning = meaning,
			pronunciation = pronunciation,
			exampleSentence = exampleSentence,
			audioUrl = audioUrl,
			difficulty = difficulty
		)
	}
}
