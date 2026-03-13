package com.example.ebook.data.model

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val coverUrl: String = "",
    val rating: Float = 0f,
    val categoryId: Int = 0,
    val summary: String = "",
    val pages: List<String> = emptyList()
)
