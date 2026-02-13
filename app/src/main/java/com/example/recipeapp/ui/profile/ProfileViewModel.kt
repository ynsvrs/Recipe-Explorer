package com.example.recipeapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.domain.repository.MealPlanRepository
import com.example.recipeapp.domain.repository.RecipeRepository
import com.example.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val recipesCount: Int = 0,
    val favoritesCount: Int = 0,
    val plansCount: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val mealPlanRepository: MealPlanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeRecipes()
        observeFavorites()
        observeMealPlans()
    }

    private fun observeRecipes() {
        viewModelScope.launch {
            recipeRepository.getRecipes().collect { recipes ->
                _state.update {
                    it.copy(recipesCount = recipes.size)
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            recipeRepository.getFavoriteRecipes().collect { favorites ->
                _state.update {
                    it.copy(favoritesCount = favorites.size)
                }
            }
        }
    }

    private fun observeMealPlans() {
        viewModelScope.launch {
            mealPlanRepository.getMealPlans().collect { result ->
                if (result is Resource.Success) {
                    _state.update {
                        it.copy(plansCount = result.data?.size ?: 0)
                    }
                }
            }
        }
    }
}
