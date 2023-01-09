package com.example.overcooked.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.model.User
import com.example.overcooked.data.repositories.home.HomeRepository
import com.example.overcooked.data.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _recipesLiveData = MutableLiveData<List<Recipe>>()
    val recipesLiveData: LiveData<List<Recipe>> = _recipesLiveData
    var targetUser: User? = null

    fun getRecommendedRecipe() = viewModelScope.launch {
        homeRepository.getRecommendedRecipeListBasedOnNation().collect { response ->
            _recipesLiveData.value = response
        }
    }

    fun getTargetUser(userId: String) = viewModelScope.launch {
        targetUser = profileRepository.getTargetUser(userId)
    }
}