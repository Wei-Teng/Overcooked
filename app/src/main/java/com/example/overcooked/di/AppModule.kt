package com.example.overcooked.di

import com.example.overcooked.data.repositories.creator.CreatorRepository
import com.example.overcooked.data.repositories.creator.CreatorRepositoryImpl
import com.example.overcooked.data.repositories.home.HomeRepository
import com.example.overcooked.data.repositories.home.HomeRepositoryImpl
import com.example.overcooked.data.repositories.profile.ProfileRepository
import com.example.overcooked.data.repositories.profile.ProfileRepositoryImpl
import com.example.overcooked.data.repositories.rating.RatingRepository
import com.example.overcooked.data.repositories.rating.RatingRepositoryImp
import com.example.overcooked.data.repositories.recipe.RecipeRepository
import com.example.overcooked.data.repositories.recipe.RecipeRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideStorageRef(): StorageReference = FirebaseStorage.getInstance().reference

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideHomeRepository(homeRepositoryImpl: HomeRepositoryImpl):HomeRepository =
        homeRepositoryImpl

    @Provides
    @Singleton
    fun provideProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository =
        profileRepositoryImpl

    @Provides
    @Singleton
    fun provideRatingRepository(ratingRepositoryImpl: RatingRepositoryImp): RatingRepository =
        ratingRepositoryImpl

    @Provides
    @Singleton
    fun provideRecipeRepository(recipeRepositoryImpl: RecipeRepositoryImpl): RecipeRepository =
        recipeRepositoryImpl

    @Provides
    @Singleton
    fun provideCreatorRepository(creatorRepositoryImpl: CreatorRepositoryImpl): CreatorRepository =
        creatorRepositoryImpl
}