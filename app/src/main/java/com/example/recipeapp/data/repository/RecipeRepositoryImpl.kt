package com.example.recipeapp.data.repository

import com.example.recipeapp.data.local.RecipeDao
import com.example.recipeapp.data.local.RecipeEntity
import com.example.recipeapp.data.remote.RecipeApi
import com.example.recipeapp.domain.model.Recipe
import com.example.recipeapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class RecipeRepositoryImpl @Inject constructor(
    private val api: RecipeApi,
    private val dao: RecipeDao
) : RecipeRepository {

    override fun getRecipes(): Flow<List<Recipe>> {
        return dao.getRecipes().map { entities ->
            entities.map {
                Recipe(
                    id = it.id,
                    title = it.title,
                    image = it.image,
                    summary = null,
                    readyInMinutes = null,
                    servings = null,
                    extendedIngredients = null,
                    instructions = null,
                    isFavorite = false
                )

            }
        }
    }

    override suspend fun fetchRecipes(page: Int, query: String?) {

        val offset = page * 20

        val response = api.getRecipes(
            query = query,
            number = 20,
            offset = offset,
            apiKey = "b66c8c6ae6cc4e4f876c6a93f9320833"

        )

        val entities = response.results.map {
            RecipeEntity(
                id = it.id,
                title = it.title,
                image = it.image,
                page = page
            )
        }

        dao.insertAll(entities)
    }
    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return dao.getFavoriteRecipes().map { entities ->
            entities.map {
                Recipe(
                    id = it.id,
                    title = it.title,
                    image = it.image,
                    summary = null,
                    readyInMinutes = null,
                    servings = null,
                    extendedIngredients = null,
                    instructions = null,
                    isFavorite = true
                )
            }
        }
    }

    override suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        dao.updateFavorite(recipeId, isFavorite)
    }

    override suspend fun getRecipeDetails(id: Int): Recipe {

        val response = api.getRecipeDetails(
            id = id,
            apiKey = "b66c8c6ae6cc4e4f876c6a93f9320833"
        )

        return Recipe(
            id = response.id,
            title = response.title,
            image = response.image,
            summary = response.summary,
            readyInMinutes = response.readyInMinutes,
            servings = response.servings,
            instructions = response.instructions,
            extendedIngredients = response.extendedIngredients?.map {
                com.example.recipeapp.domain.model.Ingredient(
                    id = it.id,
                    name = it.name,
                    amount = it.amount,
                    unit = it.unit,
                    original = it.original
                )
            },
            isFavorite = false
        )
    }

}
