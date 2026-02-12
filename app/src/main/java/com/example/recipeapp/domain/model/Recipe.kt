package com.example.recipeapp.domain.model

data class Recipe(
    val id: Int,
    val title: String,
    val image: String?,
    val summary: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val extendedIngredients: List<Ingredient>?,
    val instructions: String? = null,
    val isFavorite: Boolean = false
)

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val original: String
)