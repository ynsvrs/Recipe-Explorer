package com.example.recipeapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.domain.repository.FavoritesRepository
import com.example.recipeapp.domain.repository.RecipeRepository
import com.example.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailsState(
    val recipe: com.example.recipeapp.domain.model.Recipe? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailsState())
    val state: StateFlow<RecipeDetailsState> = _state.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {

            _state.value = RecipeDetailsState(isLoading = true)

            try {
                val recipe = recipeRepository.getRecipeDetails(recipeId)
                val isFavorite = favoritesRepository.isFavorite(recipeId)

                _state.value = RecipeDetailsState(
                    recipe = recipe,
                    isLoading = false,
                    isFavorite = isFavorite
                )

            } catch (e: Exception) {
                _state.value = RecipeDetailsState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun toggleFavorite() {
        val current = _state.value
        val recipeId = current.recipe?.id ?: return

        viewModelScope.launch {

            _state.value = current.copy(
                isFavorite = !current.isFavorite
            )

            val result = if (current.isFavorite) {
                favoritesRepository.removeFavorite(recipeId)
            } else {
                favoritesRepository.addFavorite(recipeId)
            }

            if (result is Resource.Error) {
                _state.value = current
            }
        }
    }
}
