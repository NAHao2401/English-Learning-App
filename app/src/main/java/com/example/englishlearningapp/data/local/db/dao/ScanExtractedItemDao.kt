package com.example.englishlearningapp.data.local.db.dao

import androidx.room.*
import com.example.englishlearningapp.data.local.db.entity.ScanExtractedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanExtractedItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanExtractedItem(item: ScanExtractedItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanExtractedItems(items: List<ScanExtractedItemEntity>)

    @Update
    suspend fun updateScanExtractedItem(item: ScanExtractedItemEntity)

    @Delete
    suspend fun deleteScanExtractedItem(item: ScanExtractedItemEntity)

    @Query("""
        SELECT * FROM scan_extracted_items
        WHERE scan_session_id = :sessionId
        ORDER BY id ASC
    """)
    fun getItemsBySession(sessionId: Int): Flow<List<ScanExtractedItemEntity>>

    @Query("""
        UPDATE scan_extracted_items
        SET is_saved = :isSaved
        WHERE id = :itemId
    """)
    suspend fun updateSavedState(itemId: Int, isSaved: Boolean)

    @Query("DELETE FROM scan_extracted_items WHERE scan_session_id = :sessionId")
    suspend fun deleteItemsBySession(sessionId: Int)
}