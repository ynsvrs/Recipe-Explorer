package com.example.recipeapp.ui.details

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

data class RecipeDetailsState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailsState())
    val state: StateFlow<RecipeDetailsState> = _state.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        Timber.d("Loading recipe details: recipeId=$recipeId")
        viewModelScope.launch {
            _state.value = RecipeDetailsState(isLoading = true)

            // Check if recipe is favorited
            val isFavorite = favoritesRepository.isFavorite(recipeId)

            // Mock data (Partner A will replace with API call)
            Timber.d("Using mock recipe data")
            _state.value = RecipeDetailsState(
                recipe = createMockRecipe(recipeId),
                isLoading = false,
                isFavorite = isFavorite
            )
        }
    }

    fun toggleFavorite() {
        val currentIsFavorite = _state.value.isFavorite
        val recipeId = _state.value.recipe?.id ?: return

        Timber.d("Toggling favorite: $currentIsFavorite -> ${!currentIsFavorite}")

        viewModelScope.launch {
            // Optimistically update UI
            _state.value = _state.value.copy(
                isFavorite = !currentIsFavorite
            )

            // Persist to Firebase
            val result = if (currentIsFavorite) {
                favoritesRepository.removeFavorite(recipeId)
            } else {
                favoritesRepository.addFavorite(recipeId)
            }

            // Handle errors
            when (result) {
                is Resource.Success -> {
                    Timber.d("Favorite toggled successfully")
                }
                is Resource.Error -> {
                    Timber.e("Failed to toggle favorite: ${result.message}")
                    // Revert on error
                    _state.value = _state.value.copy(
                        isFavorite = currentIsFavorite
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun createMockRecipe(id: Int) = Recipe(
        id = id,
        title = "Delicious Pasta Carbonara",
        image = null,
        summary = "A classic Italian pasta dish made with eggs, cheese, pancetta, and black pepper. Simple yet incredibly flavorful!",
        readyInMinutes = 30,
        servings = 4,
        extendedIngredients = listOf(
            com.example.recipeapp.domain.model.Ingredient(
                id = 1,
                name = "Spaghetti",
                amount = 400.0,
                unit = "g",
                original = "400g spaghetti"
            ),
            com.example.recipeapp.domain.model.Ingredient(
                id = 2,
                name = "Pancetta",
                amount = 200.0,
                unit = "g",
                original = "200g pancetta, diced"
            ),
            com.example.recipeapp.domain.model.Ingredient(
                id = 3,
                name = "Eggs",
                amount = 4.0,
                unit = "large",
                original = "4 large eggs"
            ),
            com.example.recipeapp.domain.model.Ingredient(
                id = 4,
                name = "Parmesan cheese",
                amount = 100.0,
                unit = "g",
                original = "100g grated Parmesan cheese"
            )
        ),
        instructions = """
            1. Bring a large pot of salted water to boil and cook spaghetti according to package directions.
            2. While pasta cooks, fry pancetta in a large skillet until crispy.
            3. In a bowl, whisk together eggs and grated Parmesan cheese.
            4. Drain pasta, reserving 1 cup of pasta water.
            5. Add hot pasta to the skillet with pancetta.
            6. Remove from heat and quickly stir in egg mixture, adding pasta water to create a creamy sauce.
            7. Season with black pepper and serve immediately.
        """.trimIndent(),
        isFavorite = false
    )
}