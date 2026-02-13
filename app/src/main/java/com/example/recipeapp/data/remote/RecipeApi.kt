package com.example.recipeapp.data.remote
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path


interface RecipeApi {
    @GET("recipes/complexSearch")
    suspend fun getRecipes(
        @Query("query") query:String?,
        @Query("number") number:Int,
        @Query("offset") offset:Int,
        @Query("apiKey") apiKey:String
    ):RecipeResponse
    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): RecipeDetailsResponse

}