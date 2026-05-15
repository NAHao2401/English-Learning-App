package com.example.englishlearningapp.data.repository

import com.example.englishlearningapp.data.local.db.dao.ScanExtractedItemDao
import com.example.englishlearningapp.data.local.db.dao.ScanSessionDao
import com.example.englishlearningapp.data.local.db.entity.ScanExtractedItemEntity
import com.example.englishlearningapp.data.local.db.entity.ScanSessionEntity

class ScanRepository(
    private val scanSessionDao: ScanSessionDao,
    private val scanExtractedItemDao: ScanExtractedItemDao,
) {
    fun getAllScanSessions(userId: Int) = scanSessionDao.getScanSessionsByUser(userId)

    fun getExtractedItemsBySession(sessionId: Int) = scanExtractedItemDao.getItemsBySession(sessionId)

    suspend fun insertScanSession(session: ScanSessionEntity) = scanSessionDao.insertScanSession(session)

    suspend fun insertExtractedItem(item: ScanExtractedItemEntity) = scanExtractedItemDao.insertScanExtractedItem(item)
}

