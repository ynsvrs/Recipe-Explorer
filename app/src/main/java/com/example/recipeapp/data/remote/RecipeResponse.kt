package com.example.recipeapp.data.remote

data class RecipeResponse (
    val results:List <RecipeDto>,
    val totalResults:Int
)