package com.example.recipeapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.domain.model.Recipe
import com.example.recipeapp.domain.repository.FavoritesRepository
import com.example.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class FavoritesState(
    val favorites: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    fun loadFavorites() {
        Timber.d("Loading favorites")
        _state.value = FavoritesState(isLoading = true)

        viewModelScope.launch {
            repository.getFavorites().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Convert recipe IDs to Recipe objects (mock title & image)
                        val recipes = result.data?.map {
                            Recipe(
                                id = it,
                                title = "Recipe #$it", // you can fetch real recipe from RecipeRepository
                                image = null,
                                summary = null,
                                readyInMinutes = null,
                                servings = null,
                                extendedIngredients = null,
                                instructions = null,
                                isFavorite = true
                            )
                        } ?: emptyList()

                        _state.value = FavoritesState(favorites = recipes)
                    }
                    is Resource.Error -> {
                        _state.value = FavoritesState(error = result.message)
                    }
                    is Resource.Loading -> {
                        _state.value = FavoritesState(isLoading = true)
                    }
                }
            }
        }
    }

    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val currentRecipe = _state.value.favorites.find { it.id == recipeId }
            if (currentRecipe != null) {
                // Remove from favorites
                repository.removeFavorite(recipeId)
            } else {
                // Add to favorites
                repository.addFavorite(recipeId)
            }
            loadFavorites() // refresh
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
