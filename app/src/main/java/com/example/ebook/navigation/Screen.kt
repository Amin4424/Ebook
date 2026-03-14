package com.example.ebook.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Splash : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Category : Screen()

    @Serializable
    data object Wallet : Screen()

    @Serializable
    data object Library : Screen()
    @Serializable
    data object Community : Screen()

    @Serializable
    data class BookDetails(val bookId: Int) : Screen()

    @Serializable
    data class Reader(val bookId: Int) : Screen()
}

