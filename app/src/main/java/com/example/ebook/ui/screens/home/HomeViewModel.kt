package com.example.ebook.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebook.data.model.Book
import com.example.ebook.data.model.ReadingProgress
import com.example.ebook.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeUiState(
    val allBooks: List<Book> = emptyList(),
    val featuredBooks: List<Book> = emptyList(),
    val continueReading: Pair<Book, ReadingProgress>? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
        observeReadingProgress()
    }

    private fun loadBooks() {
        _uiState.update { state ->
            state.copy(
                allBooks = repository.getAllBooks(),
                featuredBooks = repository.getFeaturedBooks()
            )
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
    }
}
