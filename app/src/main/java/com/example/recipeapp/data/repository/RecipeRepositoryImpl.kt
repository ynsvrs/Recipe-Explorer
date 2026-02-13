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
            apiKey = "94797644d51140958bd1ba284a7e3566"

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


}
