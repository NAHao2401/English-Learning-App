package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.ScanSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanSession(scanSession: ScanSessionEntity): Long

    @Update
    suspend fun updateScanSession(scanSession: ScanSessionEntity)

    @Delete
    suspend fun deleteScanSession(scanSession: ScanSessionEntity)

    @Query("""
        SELECT * FROM scan_sessions
        WHERE user_id = :userId
        ORDER BY created_at DESC
    """)
    fun getScanSessionsByUser(userId: Int): Flow<List<ScanSessionEntity>>

    @Query("SELECT * FROM scan_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getScanSessionById(sessionId: Int): ScanSessionEntity?

    @Query("""
        SELECT * FROM scan_sessions
        WHERE user_id = :userId AND scan_type = :scanType
        ORDER BY created_at DESC
    """)
    fun getScanSessionsByType(userId: Int, scanType: String): Flow<List<ScanSessionEntity>>
}