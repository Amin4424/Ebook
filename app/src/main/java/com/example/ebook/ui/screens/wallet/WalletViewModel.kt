package com.example.ebook.ui.screens.wallet

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class CoinPackage(
    val id: Int,
    val coins: Int,
    val bonusCoins: Int = 0,
    val price: String,
    val description: String,
    val isHighlighted: Boolean = false
)

data class WalletUiState(
    val coinBalance: Int = 450,
    val readingStreak: Int = 5,
    val streakGoal: Int = 7,
    val packages: List<CoinPackage> = listOf(
        CoinPackage(1, 100, 0, "۲۵,۰۰۰ ت", "مناسب برای چند فصل"),
        CoinPackage(2, 500, 50, "۱۰۰,۰۰۰ ت", "+ ۵۰ سکه هدیه", isHighlighted = true),
        CoinPackage(3, 1000, 150, "۱۸۰,۰۰۰ ت", "+ ۱۵۰ سکه هدیه")
    ),
    val weekDays: List<Boolean> = listOf(true, true, true, true, true, false, false)
)

@HiltViewModel
class WalletViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()
}
