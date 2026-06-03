package com.example.englishlearningapp.core.notification

import com.example.englishlearningapp.data.repository.PushTokenRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class EnglishLearningFirebaseMessagingService : FirebaseMessagingService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        scope.launch {
            PushTokenRepository(applicationContext).registerToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data[KEY_TYPE] != TYPE_VOCABULARY_REVIEW) return

        val dueCount = message.data[KEY_DUE_COUNT]?.toIntOrNull() ?: return
        if (dueCount > 0) {
            ReviewReminderManager(applicationContext).showReviewReminder(dueCount)
        }
    }

    companion object {
        private const val KEY_TYPE = "type"
        private const val KEY_DUE_COUNT = "due_count"
        private const val TYPE_VOCABULARY_REVIEW = "vocabulary_review"
    }
}
