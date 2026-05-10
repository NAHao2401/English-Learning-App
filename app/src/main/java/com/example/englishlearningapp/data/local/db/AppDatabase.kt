package com.example.englishlearningapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.englishlearningapp.data.local.db.dao.AnswerOptionDao
import com.example.englishlearningapp.data.local.db.dao.LessonDao
import com.example.englishlearningapp.data.local.db.dao.ProgressDao
import com.example.englishlearningapp.data.local.db.dao.QuestionDao
import com.example.englishlearningapp.data.local.db.dao.ReviewHistoryDao
import com.example.englishlearningapp.data.local.db.dao.ScanExtractedItemDao
import com.example.englishlearningapp.data.local.db.dao.ScanSessionDao
import com.example.englishlearningapp.data.local.db.dao.SpeakingPracticeDao
import com.example.englishlearningapp.data.local.db.dao.TopicDao
import com.example.englishlearningapp.data.local.db.dao.UserDao
import com.example.englishlearningapp.data.local.db.dao.UserVocabularyDao
import com.example.englishlearningapp.data.local.db.dao.VocabularyDao
import com.example.englishlearningapp.data.local.db.dao.XpHistoryDao
import com.example.englishlearningapp.data.local.db.entity.AnswerOptionEntity
import com.example.englishlearningapp.data.local.db.entity.LessonEntity
import com.example.englishlearningapp.data.local.db.entity.ProgressEntity
import com.example.englishlearningapp.data.local.db.entity.QuestionEntity
import com.example.englishlearningapp.data.local.db.entity.ReviewHistoryEntity
import com.example.englishlearningapp.data.local.db.entity.ScanExtractedItemEntity
import com.example.englishlearningapp.data.local.db.entity.ScanSessionEntity
import com.example.englishlearningapp.data.local.db.entity.SpeakingPracticeEntity
import com.example.englishlearningapp.data.local.db.entity.TopicEntity
import com.example.englishlearningapp.data.local.db.entity.UserEntity
import com.example.englishlearningapp.data.local.db.entity.UserVocabularyEntity
import com.example.englishlearningapp.data.local.db.entity.VocabularyEntity
import com.example.englishlearningapp.data.local.db.entity.XpHistoryEntity

@Database(
    entities = [
        UserEntity::class,
        TopicEntity::class,
        LessonEntity::class,
        QuestionEntity::class,
        AnswerOptionEntity::class,
        VocabularyEntity::class,
        UserVocabularyEntity::class,
        ReviewHistoryEntity::class,
        SpeakingPracticeEntity::class,
        ScanSessionEntity::class,
        ScanExtractedItemEntity::class,
        ProgressEntity::class,
        XpHistoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun topicDao(): TopicDao

    abstract fun lessonDao(): LessonDao

    abstract fun questionDao(): QuestionDao

    abstract fun answerOptionDao(): AnswerOptionDao

    abstract fun vocabularyDao(): VocabularyDao

    abstract fun userVocabularyDao(): UserVocabularyDao

    abstract fun reviewHistoryDao(): ReviewHistoryDao

    abstract fun speakingPracticeDao(): SpeakingPracticeDao

    abstract fun scanSessionDao(): ScanSessionDao

    abstract fun scanExtractedItemDao(): ScanExtractedItemDao

    abstract fun progressDao(): ProgressDao

    abstract fun xpHistoryDao(): XpHistoryDao
}