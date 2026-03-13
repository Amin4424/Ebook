package com.example.ebook.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Category : Screen("category")
    data object Wallet : Screen("wallet")
    data object Library : Screen("library")
    data object BookDetails : Screen("book_details/{bookId}") {
        fun createRoute(bookId: Int) = "book_details/$bookId"
        const val DEEP_LINK = "ebook://book/{bookId}"
    }
    data object Reader : Screen("reader/{bookId}") {
        fun createRoute(bookId: Int) = "reader/$bookId"
    }
}
