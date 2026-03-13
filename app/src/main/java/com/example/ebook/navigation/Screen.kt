package com.example.ebook.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Category : Screen("category")
    data object Wallet : Screen("wallet")
    data object Reader : Screen("reader/{bookId}") {
        fun createRoute(bookId: Int) = "reader/$bookId"
    }
}
