package com.example.ebook.ui.screens.library

import androidx.lifecycle.ViewModel
import com.example.ebook.data.model.Book
import com.example.ebook.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class LibraryTab { READING, FINISHED, WANT_TO_READ }

data class LibraryUiState(
    val activeTab: LibraryTab = LibraryTab.READING,
    val readingBooks: List<Book> = emptyList(),
    val finishedBooks: List<Book> = emptyList(),
    val wantToReadBooks: List<Book> = emptyList()
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibrary()
    }

    private fun loadLibrary() {
        val all = repository.getAllBooks()
        _uiState.update {
            it.copy(
                readingBooks = all.take(3),
                finishedBooks = all.drop(3).take(3),
                wantToReadBooks = all.drop(6)
            )
        }
    }

    fun setTab(tab: LibraryTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }
}
