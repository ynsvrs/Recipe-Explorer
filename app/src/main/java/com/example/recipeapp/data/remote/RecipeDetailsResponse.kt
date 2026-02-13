package com.example.recipeapp.data.remote

data class RecipeDetailsResponse(
    val id: Int,
    val title: String,
    val image: String?,
    val summary: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val instructions: String?,
    val extendedIngredients: List<IngredientResponse>?
)

data class IngredientResponse(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val original: String
)
