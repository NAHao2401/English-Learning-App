package com.example.englishlearningapp.core.session

import android.content.Context
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object SessionManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _unauthorizedEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val unauthorizedEvent: SharedFlow<Unit> = _unauthorizedEvent

    fun handleUnauthorized(context: Context) {
        scope.launch {
            AppDataStore(context.applicationContext).clearAuthSession()
            _unauthorizedEvent.emit(Unit)
        }
    }
}