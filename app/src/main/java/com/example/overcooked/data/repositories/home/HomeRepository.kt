package com.example.overcooked.data.repositories.home

import com.example.overcooked.data.model.Recipe
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getRecommendedRecipeListBasedOnNation(): Flow<List<Recipe>>
}