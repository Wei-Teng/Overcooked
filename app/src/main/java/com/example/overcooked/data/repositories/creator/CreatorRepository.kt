package com.example.overcooked.data.repositories.creator

import com.example.overcooked.data.model.User
import kotlinx.coroutines.flow.Flow

interface CreatorRepository {
    suspend fun getPopularCreatorList() : Flow<List<User>>
}