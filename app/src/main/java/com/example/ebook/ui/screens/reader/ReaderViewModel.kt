package com.example.ebook.ui.screens.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebook.data.model.Book
import com.example.ebook.data.model.Bookmark
import com.example.ebook.data.model.Highlight
import com.example.ebook.data.model.ReadingProgress
import com.example.ebook.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ReaderTheme { DARK, LIGHT, SEPIA, OLED }

data class ReaderUiState(
    val book: Book? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val fontSize: Int = 18,
    val readerTheme: ReaderTheme = ReaderTheme.DARK,
    val isNightMode: Boolean = true,
    val isBookmarked: Boolean = false,
    val showControls: Boolean = true,
    val showAudioPlayer: Boolean = false,
    val showToc: Boolean = false,
    val showHighlightMenu: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val audioProgress: Float = 0f,
    val audioSpeed: Float = 1.0f,
    val highlights: List<Highlight> = emptyList(),
    val selectedHighlightColor: Long = 0xFFFFEB3B
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
        observeHighlights()
    }

    private fun loadBook() {
        val book = repository.getBookById(bookId)
        if (book != null) {
            _uiState.update { it.copy(book = book, totalPages = book.pages.size) }
        }
        viewModelScope.launch {
            repository.getReadingProgress(bookId).collect { progress ->
                if (progress != null) {
                    _uiState.update {
                        it.copy(currentPage = progress.currentPage.coerceIn(0, it.totalPages - 1))
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

    private fun observeHighlights() {
        viewModelScope.launch {
            _uiState.map { it.currentPage }.distinctUntilChanged().collect { page ->
                repository.getHighlights(bookId, page).collect { highlights ->
                    _uiState.update { it.copy(highlights = highlights) }
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

    fun nextPage() = goToPage(_uiState.value.currentPage + 1)
    fun previousPage() = goToPage(_uiState.value.currentPage - 1)

    fun increaseFontSize() = _uiState.update { it.copy(fontSize = (it.fontSize + 2).coerceAtMost(32)) }
    fun decreaseFontSize() = _uiState.update { it.copy(fontSize = (it.fontSize - 2).coerceAtLeast(12)) }

    fun toggleNightMode() {
        val next = when (_uiState.value.readerTheme) {
            ReaderTheme.DARK -> ReaderTheme.LIGHT
            ReaderTheme.LIGHT -> ReaderTheme.SEPIA
            ReaderTheme.SEPIA -> ReaderTheme.OLED
            ReaderTheme.OLED -> ReaderTheme.DARK
        }
        _uiState.update { it.copy(readerTheme = next, isNightMode = next == ReaderTheme.DARK || next == ReaderTheme.OLED) }
    }

    fun setReaderTheme(theme: ReaderTheme) {
        _uiState.update { it.copy(readerTheme = theme, isNightMode = theme == ReaderTheme.DARK || theme == ReaderTheme.OLED) }
    }

    fun toggleControls() = _uiState.update { it.copy(showControls = !it.showControls) }

    fun toggleAudioPlayer() = _uiState.update { it.copy(showAudioPlayer = !it.showAudioPlayer) }

    fun toggleToc() = _uiState.update { it.copy(showToc = !it.showToc) }

    fun toggleAudioPlayback() = _uiState.update { it.copy(isAudioPlaying = !it.isAudioPlaying) }

    fun setAudioSpeed(speed: Float) = _uiState.update { it.copy(audioSpeed = speed) }

    fun setAudioProgress(progress: Float) = _uiState.update { it.copy(audioProgress = progress) }

    fun setHighlightColor(colorHex: Long) = _uiState.update { it.copy(selectedHighlightColor = colorHex) }

    fun addHighlight(startIndex: Int, endIndex: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            repository.addHighlight(
                Highlight(
                    bookId = bookId,
                    page = state.currentPage,
                    startIndex = startIndex,
                    endIndex = endIndex,
                    colorHex = state.selectedHighlightColor
                )
            )
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.isBookmarked) {
                repository.getBookmarks(bookId).first().find { it.page == state.currentPage }
                    ?.let { repository.removeBookmark(it) }
            } else {
                repository.addBookmark(Bookmark(bookId = bookId, page = state.currentPage))
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
                    chapter = "فصل ${state.currentPage + 1}",
                    lastReadTimestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
