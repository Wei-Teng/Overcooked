package com.example.overcooked.data.repositories.rating

interface RatingRepository {
    suspend fun rateRecipePost(
        recipeId: String,
        rating: Float
    ): Boolean
}