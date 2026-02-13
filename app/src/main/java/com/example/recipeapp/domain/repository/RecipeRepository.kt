package com.example.recipeapp.domain.repository

import com.example.recipeapp.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    fun getRecipes(): Flow<List<Recipe>>

    suspend fun fetchRecipes(
        page: Int,
        query: String?
    )

    suspend fun getRecipeDetails(id: Int): Recipe

    fun getFavoriteRecipes(): Flow<List<Recipe>>

    suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean)
}
