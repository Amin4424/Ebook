package com.example.ebook.ui.screens.bookdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebook.data.model.Book
import com.example.ebook.data.model.Review
import com.example.ebook.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ChapterInfo(
    val index: Int,
    val title: String,
    val pageCount: Int,
    val isFree: Boolean,
    val coinPrice: Int = 15
)

data class BookDetailsUiState(
    val book: Book? = null,
    val chapters: List<ChapterInfo> = emptyList(),
    val walletBalance: Int = 450,
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val syncSuccess: Boolean? = null,
    val userReviewText: String = "",
    val userReviewStars: Int = 5,
    val showReviewDialog: Boolean = false
)

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Int = savedStateHandle.get<Int>("bookId") ?: 1

    private val _uiState = MutableStateFlow(BookDetailsUiState())
    val uiState: StateFlow<BookDetailsUiState> = _uiState.asStateFlow()

    init {
        loadBook()
        loadReviews()
    }

    private fun loadBook() {
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookById(bookId)
            val chapters = book?.pages?.mapIndexed { i, _ ->
                ChapterInfo(
                    index = i,
                    title = chapterNames.getOrElse(i) { "فصل ${i + 1}" },
                    pageCount = (10..25).random(),
                    isFree = i < 3,
                    coinPrice = 15
                )
            } ?: emptyList()
            _uiState.update { it.copy(book = book, chapters = chapters, isLoading = false) }
        }
    }

    private fun loadReviews() {
        repository.getReviews(bookId)
            .onEach { reviews -> _uiState.update { it.copy(reviews = reviews) } }
            .launchIn(viewModelScope)
    }

    fun purchaseChapter(chapterIndex: Int) {
        val balance = _uiState.value.walletBalance
        val chapter = _uiState.value.chapters.getOrNull(chapterIndex) ?: return
        if (!chapter.isFree && balance >= chapter.coinPrice) {
            val updatedChapters = _uiState.value.chapters.toMutableList()
            updatedChapters[chapterIndex] = chapter.copy(isFree = true)
            _uiState.update {
                it.copy(
                    chapters = updatedChapters,
                    walletBalance = balance - chapter.coinPrice
                )
            }
        }
    }

    fun simulateSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncSuccess = null) }
            withContext(Dispatchers.IO) {
                kotlinx.coroutines.delay(2000)
            }
            _uiState.update { it.copy(isSyncing = false, syncSuccess = true) }
        }
    }

    fun setUserReviewText(text: String) = _uiState.update { it.copy(userReviewText = text) }
    fun setUserReviewStars(stars: Int) = _uiState.update { it.copy(userReviewStars = stars) }
    fun toggleReviewDialog(show: Boolean) = _uiState.update { it.copy(showReviewDialog = show) }

    fun submitReview() {
        val state = _uiState.value
        if (state.userReviewText.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.addReview(
                Review(
                    bookId = bookId,
                    authorName = "شما",
                    stars = state.userReviewStars,
                    comment = state.userReviewText
                )
            )
        }
        _uiState.update { it.copy(userReviewText = "", userReviewStars = 5, showReviewDialog = false) }
    }

    private val chapterNames = listOf(
        "فصل اول: آشنایی", "فصل دوم: پرده نقاشی", "فصل سوم: جستجو",
        "فصل چهارم: راز چشم‌ها", "فصل پنجم: دیدار", "فصل ششم: اعتراف",
        "فصل هفتم: بازگشت", "فصل هشتم: حقیقت"
    )
}
