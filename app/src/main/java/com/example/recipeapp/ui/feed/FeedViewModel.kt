package com.example.recipeapp.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private var currentPage = 0
    private var currentQuery: String? = null

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        observeRecipes()
        loadNextPage()
    }

    private fun observeRecipes() {
        viewModelScope.launch {
            repository.getRecipes()
                .collect { recipes ->
                    _uiState.update {
                        it.copy(recipes = recipes)
                    }
                }
        }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading || _uiState.value.isEndReached) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                repository.fetchRecipes(
                    page = currentPage,
                    query = currentQuery
                )

                currentPage++

                _uiState.update { it.copy(isLoading = false) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun refresh() {
        currentPage = 0
        viewModelScope.launch {
            _uiState.update { it.copy(recipes = emptyList()) }
            loadNextPage()
        }
    }

    fun search(query: String) {
        currentQuery = query
        currentPage = 0
        refresh()
    }
}
