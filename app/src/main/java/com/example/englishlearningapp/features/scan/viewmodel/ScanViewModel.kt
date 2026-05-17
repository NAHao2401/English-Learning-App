package com.example.englishlearningapp.features.scan.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishlearningapp.BuildConfig
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.local.db.dao.ScanExtractedItemDao
import com.example.englishlearningapp.data.local.db.dao.ScanSessionDao
import com.example.englishlearningapp.data.local.db.entity.ScanExtractedItemEntity
import com.example.englishlearningapp.data.local.db.entity.ScanSessionEntity
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray

class ScanViewModel(
    private val context: Context,
    private val scanSessionDao: ScanSessionDao,
    private val scanExtractedItemDao: ScanExtractedItemDao,
    private val appDataStore: AppDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    // Gemini model
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
    )

    fun selectImage(uri: Uri) {
        // Store uri and clear any previously extracted words
        _uiState.update { it.copy(selectedImageUri = uri, extractedWords = emptyList()) }
    }

    fun analyzeImage() {
        val uri = _uiState.value.selectedImageUri ?: return
        _uiState.update { it.copy(isAnalyzing = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Đọc ảnh thành Bitmap
                val bitmap = context.contentResolver
                    .openInputStream(uri)
                    ?.use { BitmapFactory.decodeStream(it) }
                    ?: throw Exception("Unable to read image")

                // Tạo InputContent gồm ảnh + prompt
                val inputContent = content {
                    image(bitmap)
                    text("""
                        Extract 5 to 8 important English vocabulary words from this image.
                        Return ONLY a JSON array, no markdown, no explanation.
                        Format: [{"word":"...","meaning_vi":"...","example":"..."}]
                    """.trimIndent())
                }

                val response = generativeModel.generateContent(inputContent)
                val jsonText = response.text ?: throw Exception("Empty response")

                // Parse JSON
                val words = parseWordsFromJson(jsonText)
                _uiState.update { it.copy(isAnalyzing = false, extractedWords = words) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isAnalyzing = false, errorMessage = "Lỗi phân tích ảnh: ${e.message}")
                }
            }
        }
    }

    fun saveAllWords() {
        val words = _uiState.value.extractedWords
        if (words.isEmpty()) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                // Get current user ID from DataStore
                val userId = appDataStore.userId.first()
                if (userId <= 0) {
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = "Chưa đăng nhập")
                    }
                    return@launch
                }

                // Lưu session
                val session = ScanSessionEntity(
                    userId = userId,
                    imageUri = _uiState.value.selectedImageUri?.toString(),
                    extractedText = words.joinToString(", ") { it.word },
                    scanType = "vocabulary",
                    createdAt = System.currentTimeMillis()
                )
                val sessionId = scanSessionDao.insertScanSession(session)

                // Lưu từng từ
                words.forEach { word ->
                    scanExtractedItemDao.insertScanExtractedItem(
                        ScanExtractedItemEntity(
                            scanSessionId = sessionId.toInt(),
                            word = word.word,
                            meaning = word.meaningVi,
                            exampleSentence = word.example
                        )
                    )
                }

                _uiState.update { it.copy(isSaving = false, isSaveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = "Lỗi lưu dữ liệu: ${e.message}")
                }
            }
        }
    }

    private fun parseWordsFromJson(json: String): List<ExtractedWord> {
        // Xóa markdown fence nếu Gemini trả về ```json ... ```
        val clean = json.replace(Regex("```json|```"), "").trim()
        val arr = JSONArray(clean)
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            ExtractedWord(
                word = obj.getString("word"),
                meaningVi = obj.getString("meaning_vi"),
                example = obj.optString("example", "")
            )
        }
    }
}