package com.example.overcooked.data.repositories.home

import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : HomeRepository {
    private val usersCollection = firestore.collection("users")
    private val recipesCollection = firestore.collection("recipes")

    override suspend fun getRecommendedRecipeListBasedOnNation() = callbackFlow {
        val userSnapshot = usersCollection.document(firebaseAuth.uid.toString()).get().await()
        val user = userSnapshot.toObject(User::class.java) ?: throw Exception()
        val snapshotList = usersCollection.whereEqualTo("region", user.region).get().await()
        val targetUserIdList = arrayListOf<String>()
        for (snapshot in snapshotList) {
            if (snapshot.get("id")!!.toString() != user.id)
                targetUserIdList.add(snapshot.get("id")!!.toString())
        }
        if (targetUserIdList.isEmpty())
            targetUserIdList.add("1")
        val snapshotListener =
            recipesCollection.whereIn("authorId", targetUserIdList)
                .addSnapshotListener { snapshot, e ->
                    val recipesList = if (snapshot != null) {
                        val recipe = snapshot.toObjects(Recipe::class.java)
                        recipe
                    } else {
                        throw Exception(e?.message)
                    }
                    trySend(recipesList)
                }

        awaitClose {
            snapshotListener.remove()
        }
    }


}