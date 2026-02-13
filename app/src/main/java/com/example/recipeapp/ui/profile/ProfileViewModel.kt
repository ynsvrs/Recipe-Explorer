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
        observeStatistics()
    }

    private fun observeStatistics() {
        viewModelScope.launch {

            combine(
                recipeRepository.getRecipes(),
                recipeRepository.getFavoriteRecipes(),
                mealPlanRepository.getMealPlans()
            ) { recipes, favorites, plansResult ->

                val plansCount = if (plansResult is Resource.Success) {
                    plansResult.data?.size ?: 0
                } else 0

                ProfileState(
                    recipesCount = recipes.size,
                    favoritesCount = favorites.size,
                    plansCount = plansCount
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
