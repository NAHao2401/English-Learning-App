package com.example.englishlearningapp.di

import android.content.Context
import com.example.englishlearningapp.data.local.db.AppDatabase
import com.example.englishlearningapp.data.local.db.DatabaseProvider
import com.example.englishlearningapp.data.local.db.dao.TopicDao
import com.example.englishlearningapp.data.local.db.dao.UserVocabularyDao
import com.example.englishlearningapp.data.local.db.dao.VocabularyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return DatabaseProvider.getDatabase(context)
    }

    @Provides
    fun provideTopicDao(database: AppDatabase): TopicDao {
        return database.topicDao()
    }

    @Provides
    fun provideVocabularyDao(database: AppDatabase): VocabularyDao {
        return database.vocabularyDao()
    }

    @Provides
    fun provideUserVocabularyDao(database: AppDatabase): UserVocabularyDao {
        return database.userVocabularyDao()
    }
}