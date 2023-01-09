package com.example.overcooked.data.repositories.recipe

import com.example.overcooked.data.model.Recipe

interface RecipeRepository {
    suspend fun getCurrentRecipe(recipeId: String): Recipe
    suspend fun postRecipe(recipe: Recipe): Boolean
    suspend fun editRecipe(recipe: Recipe, recipeDetails: HashMap<String, Any>): Boolean
    suspend fun deleteRecipe(recipe: Recipe): Boolean
    suspend fun saveRecipe(recipe: Recipe): Boolean
    suspend fun unsaveRecipe(recipe: Recipe): Boolean
    suspend fun launchRecipeReport(recipe: Recipe, reason: String)
}