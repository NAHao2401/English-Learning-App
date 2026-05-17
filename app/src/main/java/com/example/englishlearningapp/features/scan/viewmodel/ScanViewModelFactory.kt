package com.example.englishlearningapp.features.scan.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.englishlearningapp.data.local.db.DatabaseProvider
import com.example.englishlearningapp.data.local.datastore.AppDataStore

class ScanViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = DatabaseProvider.getDatabase(context)
        val appDataStore = AppDataStore(context.applicationContext)
        return ScanViewModel(
            context = context,
            scanSessionDao = db.scanSessionDao(),
            scanExtractedItemDao = db.scanExtractedItemDao(),
            appDataStore = appDataStore
        ) as T
    }
}