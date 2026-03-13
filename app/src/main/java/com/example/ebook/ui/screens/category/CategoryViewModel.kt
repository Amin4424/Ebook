package com.example.ebook.ui.screens.category

import androidx.lifecycle.ViewModel
import com.example.ebook.data.model.Category
import com.example.ebook.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val expandedCategoryIds: Set<Int> = emptySet()
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _uiState.update { it.copy(categories = repository.getCategories()) }
    }

    fun toggleCategory(categoryId: Int) {
        _uiState.update { state ->
            val expanded = state.expandedCategoryIds.toMutableSet()
            if (expanded.contains(categoryId)) {
                expanded.remove(categoryId)
            } else {
                expanded.add(categoryId)
            }
            state.copy(expandedCategoryIds = expanded)
        }
    }
}
