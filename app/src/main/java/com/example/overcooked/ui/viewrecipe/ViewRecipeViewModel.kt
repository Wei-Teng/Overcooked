package com.example.overcooked.ui.viewrecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.model.User
import com.example.overcooked.data.repositories.profile.ProfileRepository
import com.example.overcooked.data.repositories.rating.RatingRepository
import com.example.overcooked.data.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val profileRepository: ProfileRepository,
    private val ratingRepository: RatingRepository
) : ViewModel() {

    private val _recipeUserPair = MutableLiveData<Pair<Recipe, User>>()
    val recipeUserPair: LiveData<Pair<Recipe, User>> = _recipeUserPair
    var currentUser: User? = null

    fun getCurrentRecipe(recipeId: String) = viewModelScope.launch {
        val recipeData = recipeRepository.getCurrentRecipe(recipeId)
        val userData = profileRepository.getTargetUser(recipeData.authorId)
        _recipeUserPair.value = Pair(recipeData, userData)
    }

    fun getCurrentUser() = viewModelScope.launch {
        currentUser = profileRepository.getCurrentUser()
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeRepository.deleteRecipe(recipe)
    }

    fun followSomeoneProfile() = viewModelScope.launch {
        profileRepository.followSomeoneProfile(
            profileRepository.getCurrentUser(),
            recipeUserPair.value!!.second
        )
    }

    fun unfollowSomeoneProfile() = viewModelScope.launch {
        profileRepository.unfollowSomeoneProfile(
            profileRepository.getCurrentUser(),
            recipeUserPair.value!!.second
        )
    }

    fun editRecipe(recipe: Recipe, recipeDetails: HashMap<String, Any>) = viewModelScope.launch {
        recipeRepository.editRecipe(recipe, recipeDetails)
    }

    fun saveRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeRepository.saveRecipe(recipe)
    }

    fun unsaveRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeRepository.unsaveRecipe(recipe)
    }

    fun rateRecipePost(recipeId: String, rating: Float) = viewModelScope.launch {
        ratingRepository.rateRecipePost(recipeId, rating)
    }

    fun launchReport(recipe: Recipe, reason: String) = viewModelScope.launch {
        recipeRepository.launchRecipeReport(recipe, reason)
    }
}