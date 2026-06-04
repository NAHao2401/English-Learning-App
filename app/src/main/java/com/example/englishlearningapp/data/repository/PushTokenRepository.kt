package com.example.englishlearningapp.data.repository

import android.content.Context
import com.example.englishlearningapp.data.local.datastore.AppDataStore
import com.example.englishlearningapp.data.remote.api.RetrofitClient
import com.example.englishlearningapp.data.remote.api.request.DeviceTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine

class PushTokenRepository(context: Context) {

    private val appContext = context.applicationContext
    private val appDataStore = AppDataStore(appContext)
    init {
        RetrofitClient.init(appContext)
    }
    private val notificationApi = RetrofitClient.notificationApiService

    suspend fun registerCurrentToken() {
        getCurrentToken()?.let { registerToken(it) }
    }

    suspend fun registerToken(token: String) {
        if (!appDataStore.isLoggedIn.first()) return
        runCatching {
            notificationApi.registerDeviceToken(DeviceTokenRequest(token))
        }
    }

    suspend fun unregisterCurrentToken() {
        getCurrentToken()?.let { token ->
            runCatching {
                notificationApi.unregisterDeviceToken(DeviceTokenRequest(token))
            }
        }
    }

    private suspend fun getCurrentToken(): String? = suspendCancellableCoroutine { continuation ->
        runCatching {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (continuation.isActive) {
                        val token = if (task.isSuccessful) task.result else null
                        continuation.resume(token?.takeIf { it.isNotBlank() })
                    }
                }
        }.onFailure {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }
    }
}
