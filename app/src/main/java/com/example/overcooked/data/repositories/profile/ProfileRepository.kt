package com.example.overcooked.data.repositories.profile

import android.net.Uri
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getCurrentUser(): User
    suspend fun getTargetUser(id: String): User
    suspend fun uploadImage(uri: Uri)
    suspend fun uploadName(newName: String)
    suspend fun uploadBio(newBio: String)
    suspend fun followSomeoneProfile(currentUser: User, targetUser: User): User
    suspend fun unfollowSomeoneProfile(currentUser: User, targetUser: User): User
    suspend fun getRecipe(userId: String): Flow<List<Recipe>>
    suspend fun getMySavedRecipe(): Flow<List<Recipe>>
    suspend fun getFollowingList(): Flow<List<User>>
    suspend fun getFollowerList(): Flow<List<User>>
}