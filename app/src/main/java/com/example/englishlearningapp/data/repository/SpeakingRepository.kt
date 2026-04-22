package com.example.englishlearningapp.data.repository

import com.example.englishlearningapp.data.local.db.dao.ReviewHistoryDao
import com.example.englishlearningapp.data.local.db.dao.SpeakingPracticeDao
import com.example.englishlearningapp.data.local.db.entity.ReviewHistoryEntity
import com.example.englishlearningapp.data.local.db.entity.SpeakingPracticeEntity

class SpeakingRepository(
    private val speakingPracticeDao: SpeakingPracticeDao,
    private val reviewHistoryDao: ReviewHistoryDao,
) {
    fun getPracticeByUserId(userId: Int) = speakingPracticeDao.getPracticesByUser(userId)

    fun getReviewHistoryByUserId(userId: Int) = reviewHistoryDao.getReviewHistoriesByUser(userId)

    suspend fun insertPractice(practice: SpeakingPracticeEntity) = speakingPracticeDao.insertSpeakingPractice(practice)

    suspend fun insertReview(review: ReviewHistoryEntity) = reviewHistoryDao.insertReviewHistory(review)
}

