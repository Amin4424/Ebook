package com.example.ebook.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebook.data.model.Book
import com.example.ebook.data.model.ReadingProgress
import com.example.ebook.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val allBooks: List<Book> = emptyList(),
    val featuredBooks: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val continueReading: Pair<Book, ReadingProgress>? = null,
    val searchQuery: String = "",
    val totalBooksFinished: Int = 2,
    val readingStreakDays: Int = 5,
    val totalMinutesRead: Int = 1240,
    val dailyGoalPages: Int = 20,
    val pagesReadToday: Int = 7,
    val showGoalDialog: Boolean = false,
    val goalInputText: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Filtered books derived from allBooks + searchQuery for performance
    val filteredBooks: StateFlow<List<Book>> = _uiState
        .map { state ->
            if (state.searchQuery.isBlank()) state.allBooks
            else state.allBooks.filter {
                it.title.contains(state.searchQuery, ignoreCase = true) ||
                        it.author.contains(state.searchQuery, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadBooks()
        observeReadingProgress()
    }

    private fun loadBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = repository.getAllBooks()
            val featured = repository.getFeaturedBooks()
            _uiState.update { state ->
                state.copy(allBooks = all, featuredBooks = featured, filteredBooks = all)
            }
        }
    }

    private fun observeReadingProgress() {
        repository.getLastReadProgress()
            .onEach { progress ->
                if (progress != null) {
                    val book = repository.getBookById(progress.bookId)
                    if (book != null) {
                        _uiState.update { it.copy(continueReading = Pair(book, progress)) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // Heavy filtering happens in filteredBooks StateFlow via Dispatchers.Default (map runs on collector thread)
    }

    fun showGoalDialog(show: Boolean) {
        _uiState.update { it.copy(showGoalDialog = show, goalInputText = it.dailyGoalPages.toString()) }
    }

    fun updateGoalInput(text: String) {
        _uiState.update { it.copy(goalInputText = text) }
    }

    fun saveGoal() {
        val pages = _uiState.value.goalInputText.toIntOrNull() ?: return
        _uiState.update { it.copy(dailyGoalPages = pages, showGoalDialog = false) }
    }
}
