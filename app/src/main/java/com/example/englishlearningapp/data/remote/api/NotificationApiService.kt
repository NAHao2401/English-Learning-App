package com.example.englishlearningapp.data.remote.api

import com.example.englishlearningapp.data.remote.api.request.DeviceTokenRequest
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface NotificationApiService {

    @POST("notifications/device-tokens")
    suspend fun registerDeviceToken(@Body request: DeviceTokenRequest)

    @HTTP(method = "DELETE", path = "notifications/device-tokens", hasBody = true)
    suspend fun unregisterDeviceToken(@Body request: DeviceTokenRequest)
}
