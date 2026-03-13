package com.example.ebook.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CoinPackage(
    val id: Int,
    val coins: Int,
    val bonusCoins: Int = 0,
    val price: String,
    val description: String,
    val isHighlighted: Boolean = false
)

data class Transaction(
    val id: Int,
    val description: String,
    val amount: String,
    val isDeposit: Boolean,
    val date: String
)

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
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
    val weekDays: List<Boolean> = listOf(true, true, true, true, true, false, false),
    val transactions: List<Transaction> = listOf(
        Transaction(1, "خرید کتاب: چشمهایش", "- ۵۰ سکه", false, "۱۴۰۳/۱۲/۲۲"),
        Transaction(2, "شارژ کیف پول", "+ ۵۰۰ سکه", true, "۱۴۰۳/۱۲/۲۰"),
        Transaction(3, "خرید کتاب: بوف کور", "- ۸۰ سکه", false, "۱۴۰۳/۱۲/۱۸"),
        Transaction(4, "شارژ کیف پول", "+ ۱۰۰ سکه", true, "۱۴۰۳/۱۲/۱۵"),
        Transaction(5, "خرید کتاب: شازده کوچولو", "- ۴۵ سکه", false, "۱۴۰۳/۱۲/۱۰"),
        Transaction(6, "هدیه ثبت‌نام", "+ ۲۵ سکه", true, "۱۴۰۳/۱۲/۰۱"),
    ),
    val achievements: List<Achievement> = listOf(
        Achievement(1, "جغد شب", "بعد از ساعت ۱۲ شب مطالعه کن", "🦉", isUnlocked = true),
        Achievement(2, "کتابخوان حرفه‌ای", "۱۰ کتاب بخوان", "📚", isUnlocked = false),
        Achievement(3, "زنجیره ۷ روزه", "۷ روز پشت سر هم مطالعه کن", "🔥", isUnlocked = false),
        Achievement(4, "خواننده سریع", "یک کتاب را در یک روز تمام کن", "⚡", isUnlocked = true),
        Achievement(5, "کاشف", "اولین کتاب رو بخوان", "🔭", isUnlocked = true),
        Achievement(6, "منتقد", "۵ نظر بنویس", "✍️", isUnlocked = false),
    ),
    val isSyncing: Boolean = false,
    val syncSuccess: Boolean? = null
)

@HiltViewModel
class WalletViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    fun simulateSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncSuccess = null) }
            withContext(Dispatchers.IO) { delay(2000) }
            _uiState.update { it.copy(isSyncing = false, syncSuccess = true) }
        }
    }
}
