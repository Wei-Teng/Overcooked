package com.example.overcooked.data.repositories.creator

import com.example.overcooked.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreatorRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore
) : CreatorRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun getPopularCreatorList() = callbackFlow {
        val snapshotListener =
            usersCollection.whereGreaterThan("followerNum", -1)
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
}