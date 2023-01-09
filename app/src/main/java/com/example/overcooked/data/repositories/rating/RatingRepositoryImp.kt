package com.example.overcooked.data.repositories.rating

import android.util.Log
import com.example.overcooked.data.repositories.profile.ProfileRepository
import com.example.overcooked.data.repositories.recipe.RecipeRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class RatingRepositoryImp @Inject constructor(
    firestore: FirebaseFirestore,
    private val recipeRepository: RecipeRepository,
    private val profileRepository: ProfileRepository
) : RatingRepository {

    private val recipesCollection = firestore.collection("recipes")

    override suspend fun rateRecipePost(
        recipeId: String,
        rating: Float,
    ): Boolean {
        return try {
            val recipe = recipeRepository.getCurrentRecipe(recipeId)
            val user = profileRepository.getCurrentUser()
            val newRating =
                ((recipe.rating * recipe.reviewNum + rating) / (recipe.reviewNum + 1) * 10).roundToInt() / 10.0
            recipe.raterIdList.add(user.id)
            recipesCollection.document(recipe.id).update(
                mapOf(
                    "rating" to newRating,
                    "reviewNum" to recipe.reviewNum + 1,
                    "raterIdList" to recipe.raterIdList
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e("RatingRepositoryImp", "rateRecipePost ${e.message}")
            false
        }
    }
}