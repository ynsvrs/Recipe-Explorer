package com.example.recipeapp.domain.repository
import com.example.recipeapp.domain.model.MealPlan
import com.example.recipeapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface MealPlanRepository {
    suspend fun addMealPlan(mealPlan: MealPlan): Resource<String>
    suspend fun updateMealPlan(mealPlan: MealPlan): Resource<Unit>
    suspend fun deleteMealPlan(mealPlanId: String): Resource<Unit>
    fun getMealPlans(): Flow<Resource<List<MealPlan>>>
}