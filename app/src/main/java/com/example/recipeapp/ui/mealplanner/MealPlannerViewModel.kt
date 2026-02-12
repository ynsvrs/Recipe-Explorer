package com.example.recipeapp.ui.mealplanner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.domain.model.DayOfWeek
import com.example.recipeapp.domain.model.MealPlan
import com.example.recipeapp.domain.model.MealType
import com.example.recipeapp.domain.repository.MealPlanRepository
import com.example.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MealPlannerState(
    val mealPlans: List<MealPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class MealPlannerViewModel @Inject constructor(
    private val repository: MealPlanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MealPlannerState())
    val state: StateFlow<MealPlannerState> = _state.asStateFlow()

    init {
        loadMealPlans()
    }

    private fun loadMealPlans() {
        viewModelScope.launch {
            repository.getMealPlans().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = MealPlannerState(
                            mealPlans = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _state.value = MealPlannerState(
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = MealPlannerState(isLoading = true)
                    }
                }
            }
        }
    }

    fun addMealPlan(
        recipeId: Int,
        recipeTitle: String,
        recipeImage: String?,
        day: DayOfWeek,
        mealType: MealType
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val mealPlan = MealPlan(
                recipeId = recipeId,
                recipeTitle = recipeTitle,
                recipeImage = recipeImage,
                day = day.displayName,
                mealType = mealType.displayName
            )

            when (val result = repository.addMealPlan(mealPlan)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Meal plan added successfully"
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteMealPlan(mealPlanId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteMealPlan(mealPlanId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        successMessage = "Meal plan deleted"
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            error = null,
            successMessage = null
        )
    }
}