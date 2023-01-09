package com.example.overcooked.data.repositories.profile

import android.net.Uri
import android.util.Log
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val storageReference: StorageReference,
    private val firebaseStorage: FirebaseStorage,
    firestore: FirebaseFirestore
) : ProfileRepository {

    private val usersCollection = firestore.collection("users")
    private val recipesCollection = firestore.collection("recipes")

    override suspend fun getCurrentUser(): User {
        return try {
            val snapshot = usersCollection.document(firebaseAuth.uid.toString()).get().await()
            val user = snapshot.toObject(User::class.java) ?: throw Exception()
            user
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImpl", "getCurrentUser: ${e.message}")
            User()
        }
    }

    override suspend fun getTargetUser(id: String): User {
        return try {
            val snapshotList = usersCollection.whereEqualTo("id", id)
                .get().await()
            val user = snapshotList.documents[0].toObject(User::class.java) ?: throw Exception()
            user
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImpl", "getTargetUser: ${e.message}")
            User()
        }
    }

    override suspend fun uploadImage(uri: Uri) {
        try {
            val childRef =
                storageReference.child("ProfileImages").child(System.currentTimeMillis().toString())
            val uploadTask = childRef.putFile(uri).await()
            val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await() ?: throw Exception()

            val snapshot = usersCollection.document(firebaseAuth.uid.toString()).get().await()
            val user = snapshot.toObject(User::class.java) ?: throw Exception()
            val newUser = user.copy(image = imageUrl.toString())

            usersCollection.document(firebaseAuth.uid.toString()).set(newUser).await()
            if (user.image.isNotEmpty()) {
                val imageRef = firebaseStorage.getReferenceFromUrl(user.image)
                imageRef.delete().await()
            }
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImp", "uploadImageAndReturnUser: ${e.message}")
        }
    }

    override suspend fun uploadName(newName: String) {
        try {
            usersCollection.document(firebaseAuth.uid.toString())
                .update(mapOf("username" to newName)).await()
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImp", "uploadImageAndReturnUser: ${e.message}")
        }
    }

    override suspend fun uploadBio(newBio: String) {
        try {
            usersCollection.document(firebaseAuth.uid.toString())
                .update(mapOf("bio" to newBio)).await()
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImp", "uploadImageAndReturnUser: ${e.message}")
        }
    }

    override suspend fun followSomeoneProfile(currentUser: User, targetUser: User): User {
        return try {
            currentUser.followingUserIdList.add(targetUser.id)
            usersCollection.document(firebaseAuth.uid.toString())
                .update(
                    hashMapOf(
                        "followingNum" to currentUser.followingNum + 1,
                        "followingUserIdList" to currentUser.followingUserIdList
                    ) as Map<String, Any>
                ).await()
            val snapshotList = usersCollection.whereEqualTo("id", targetUser.id)
                .get().await()
            targetUser.followerUserIdList.add(currentUser.id)
            snapshotList.documents[0].reference.update(
                mapOf(
                    "followerNum" to targetUser.followerNum + 1,
                    "followerUserIdList" to targetUser.followerUserIdList
                )
            ).await()
            getCurrentUser()
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImp", "followSomeoneProfile ${e.message}")
            User()
        }
    }

    override suspend fun unfollowSomeoneProfile(currentUser: User, targetUser: User): User {
        return try {
            currentUser.followingUserIdList.remove(targetUser.id)
            usersCollection.document(firebaseAuth.uid.toString())
                .update(
                    hashMapOf(
                        "followingNum" to currentUser.followingNum - 1,
                        "followingUserIdList" to currentUser.followingUserIdList
                    ) as Map<String, Any>
                ).await()
            val snapshotList = usersCollection.whereEqualTo("id", targetUser.id)
                .get().await()
            targetUser.followerUserIdList.remove(currentUser.id)
            snapshotList.documents[0].reference.update(
                mapOf(
                    "followerNum" to targetUser.followerNum - 1,
                    "followerUserIdList" to targetUser.followerUserIdList
                )
            ).await()
            getCurrentUser()
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImp", "unfollowSomeoneProfile ${e.message}")
            User()
        }
    }

    override suspend fun getRecipe(userId: String) = callbackFlow {
        val snapshotListener = recipesCollection.whereEqualTo("authorId", userId)
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

    override suspend fun getMySavedRecipe() = callbackFlow {
        val user = getCurrentUser()
        val snapshotListener = recipesCollection.whereArrayContains("savedUserIdList", user.id)
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

    override suspend fun getFollowingList() = callbackFlow {
        val user = getCurrentUser()
        val snapshotListener =
            usersCollection.whereArrayContains("followerUserIdList", user.id)
                .addSnapshotListener { snapshot, e ->
                    val creatorsList = if (snapshot != null) {
                        val creators = snapshot.toObjects(User::class.java)
                        creators
                    } else {
                        throw Exception(e?.message)
                    }
                    trySend(creatorsList)
                }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getFollowerList() = callbackFlow {
        val user = getCurrentUser()
        if (user.followerUserIdList.isEmpty())
            user.followerUserIdList.add("1")
        val snapshotListener =
            usersCollection.whereIn("id", user.followerUserIdList)
                .addSnapshotListener { snapshot, e ->
                    val creatorsList = if (snapshot != null) {
                        val creators = snapshot.toObjects(User::class.java)
                        creators
                    } else {
                        throw Exception(e?.message)
                    }
                    trySend(creatorsList)
                }
        if (user.followerUserIdList.contains("1"))
            user.followerUserIdList.remove("1")
        awaitClose {
            snapshotListener.remove()
        }
    }
}