package com.example.englishlearningapp.data.remote.api.request

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,

    @SerializedName("new_password")
    val newPassword: String
)