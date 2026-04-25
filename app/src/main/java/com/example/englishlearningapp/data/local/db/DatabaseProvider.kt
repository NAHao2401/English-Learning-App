package com.example.englishlearningapp.data.local.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    //Đảm bảo các thread đều thấy giá trị mới nhất của INSTANCE
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext, //Tránh leak memory
                AppDatabase::class.java,
                "english_learning_db"
            )
                .addCallback(DatabaseSeeder.callback(context.applicationContext))
                // XÓA DB nếu version thay đổi (dễ dev, không cần migration)
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            instance
        }
    }
}