package com.example.overcooked.data.repositories.recipe

import android.util.Log
import androidx.core.net.toUri
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.repositories.profile.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storageReference: StorageReference,
    private val profileRepository: ProfileRepository
) : RecipeRepository {

    private val recipesCollection = firestore.collection("recipes")

    override suspend fun getCurrentRecipe(recipeId: String): Recipe {
        return try {
            val snapshotList = recipesCollection.whereEqualTo("id", recipeId).get().await()
            val recipe = snapshotList.documents[0].toObject(Recipe::class.java) ?: throw Exception()
            recipe
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImpl", "getCurrentRecipe: ${e.message}")
            Recipe()
        }
    }

    override suspend fun postRecipe(recipe: Recipe): Boolean {
        return try {
            val childRef =
                storageReference.child("RecipeImages").child(System.currentTimeMillis().toString())
            val uploadTask = childRef.putFile(recipe.image.toUri()).await()
            val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await() ?: throw Exception()
            recipe.authorId = profileRepository.getCurrentUser().id
            recipe.image = imageUrl.toString()
            recipesCollection.document(recipe.id).set(recipe).await()
            true
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImp", "submitRecipe ${e.message}")
            false
        }
    }

    override suspend fun editRecipe(recipe: Recipe, recipeDetails: HashMap<String, Any>): Boolean {
        return try {
            val childRef =
                storageReference.child("RecipeImages").child(System.currentTimeMillis().toString())
            val uploadTask = childRef.putFile(recipeDetails["image"].toString().toUri()).await()
            val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await() ?: throw Exception()
            recipeDetails["image"] = imageUrl.toString()
            if (recipe.image.isNotEmpty()) {
                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(recipe.image)
                imageRef.delete().await()
            }

            recipesCollection.document(recipe.id).update(
                recipeDetails as Map<String, Any>
            ).await()
            true
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImp", "editRecipe ${e.message}")
            false
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe): Boolean {
        return try {
            recipesCollection.document(recipe.id).delete().await()
            true
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImp", "deleteRecipe ${e.message}")
            false
        }
    }

    override suspend fun saveRecipe(recipe: Recipe): Boolean {
        return try {
            recipe.savedUserIdList.add(profileRepository.getCurrentUser().id)
            recipesCollection.document(recipe.id).update(
                mapOf("savedUserIdList" to recipe.savedUserIdList)
            ).await()
            true
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImp", "saveRecipe ${e.message}")
            false
        }
    }

    override suspend fun unsaveRecipe(recipe: Recipe): Boolean {
        return try {
            recipe.savedUserIdList.remove(profileRepository.getCurrentUser().id)
            recipesCollection.document(recipe.id).update(
                mapOf("savedUserIdList" to recipe.savedUserIdList)
            ).await()
            true
        } catch (e: Exception) {
            Log.e("RecipeRepositoryImp", "unsaveRecipe ${e.message}")
            false
        }
    }

    override suspend fun launchRecipeReport(recipe: Recipe, reason: String) {
        val reportId = UUID.randomUUID().toString()
        firestore.collection("reports")
            .document(reportId)
            .set(
                mapOf(
                    "id" to reportId,
                    "recipeId" to recipe.id,
                    "recipeImage" to recipe.image,
                    "recipeTitle" to recipe.title,
                    "reason" to reason,
                    "authorId" to recipe.authorId
                )
            ).await()
    }
}