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
    private var isLoadingMore = false

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
                        it.copy(
                            recipes = recipes,
                            isEndReached = recipes.isEmpty()
                        )
                    }
                }
        }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading || isLoadingMore) return

        viewModelScope.launch {
            try {
                isLoadingMore = true
                _uiState.update { it.copy(isLoading = true, error = null) }

                repository.fetchRecipes(
                    page = currentPage,
                    query = currentQuery
                )

                currentPage++

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Unknown error"
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
                isLoadingMore = false
            }
        }
    }


    fun refresh() {
        currentPage = 0
        viewModelScope.launch {
            _uiState.update { it.copy(recipes = emptyList(), isEndReached = false) }
            loadNextPage()
        }
    }

    fun search(query: String) {
        currentQuery = query
        currentPage = 0
        refresh()
    }
    fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId, isFavorite)
        }
    }

}
