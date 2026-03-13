package com.example.ebook.ui.screens.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebook.data.model.Book
import com.example.ebook.data.model.Bookmark
import com.example.ebook.data.model.ReadingProgress
import com.example.ebook.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReaderUiState(
    val book: Book? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val fontSize: Int = 18,
    val isNightMode: Boolean = false,
    val isBookmarked: Boolean = false,
    val showControls: Boolean = true
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Int = savedStateHandle.get<Int>("bookId") ?: 1

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        loadBook()
        observeBookmark()
    }

    private fun loadBook() {
        val book = repository.getBookById(bookId)
        if (book != null) {
            _uiState.update {
                it.copy(
                    book = book,
                    totalPages = book.pages.size
                )
            }
        }

        // Restore reading progress
        viewModelScope.launch {
            repository.getReadingProgress(bookId).collect { progress ->
                if (progress != null) {
                    _uiState.update {
                        it.copy(
                            currentPage = progress.currentPage.coerceIn(0, it.totalPages - 1),
                            fontSize = if (progress.chapter.startsWith("fontSize:")) {
                                progress.chapter.removePrefix("fontSize:").toIntOrNull() ?: 18
                            } else 18
                        )
                    }
                }
            }
        }
    }

    private fun observeBookmark() {
        viewModelScope.launch {
            _uiState.collectLatest { state ->
                repository.isBookmarked(bookId, state.currentPage).collect { isBookmarked ->
                    _uiState.update { it.copy(isBookmarked = isBookmarked) }
                }
            }
        }
    }

    fun goToPage(page: Int) {
        val totalPages = _uiState.value.totalPages
        if (page in 0 until totalPages) {
            _uiState.update { it.copy(currentPage = page) }
            saveProgress()
        }
    }

    fun nextPage() {
        goToPage(_uiState.value.currentPage + 1)
    }

    fun previousPage() {
        goToPage(_uiState.value.currentPage - 1)
    }

    fun increaseFontSize() {
        _uiState.update {
            it.copy(fontSize = (it.fontSize + 2).coerceAtMost(32))
        }
    }

    fun decreaseFontSize() {
        _uiState.update {
            it.copy(fontSize = (it.fontSize - 2).coerceAtLeast(12))
        }
    }

    fun toggleNightMode() {
        _uiState.update { it.copy(isNightMode = !it.isNightMode) }
    }

    fun toggleControls() {
        _uiState.update { it.copy(showControls = !it.showControls) }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.isBookmarked) {
                repository.getBookmarks(bookId).first().find { it.page == state.currentPage }
                    ?.let { repository.removeBookmark(it) }
            } else {
                repository.addBookmark(
                    Bookmark(bookId = bookId, page = state.currentPage)
                )
            }
        }
    }

    private fun saveProgress() {
        viewModelScope.launch {
            val state = _uiState.value
            repository.saveReadingProgress(
                ReadingProgress(
                    bookId = bookId,
                    currentPage = state.currentPage,
                    totalPages = state.totalPages,
                    chapter = "fontSize:${state.fontSize}",
                    lastReadTimestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
