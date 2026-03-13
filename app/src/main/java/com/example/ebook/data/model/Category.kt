package com.example.ebook.data.model

data class Category(
    val id: Int,
    val name: String,
    val icon: String = "📚",
    val books: List<Book> = emptyList()
)
