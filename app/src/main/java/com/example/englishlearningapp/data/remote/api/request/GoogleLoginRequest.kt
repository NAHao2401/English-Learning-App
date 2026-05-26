package com.example.englishlearningapp.data.remote.api.request

import com.google.gson.annotations.SerializedName

data class GoogleLoginRequest(
    @SerializedName("id_token")
    val idToken: String
)