package com.example.englishlearningapp.data.remote.api.response

data class PaginatedResponse<T>(
    val items: List<T>,
    val page: Int,
    val limit: Int,
    val total: Int,
    val total_pages: Int
)