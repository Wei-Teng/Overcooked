package com.example.overcooked.ui.savedrecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.model.User
import com.example.overcooked.data.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedRecipeViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {
    private val _savedRecipeList = MutableLiveData<List<Recipe>>()
    val savedRecipeList: LiveData<List<Recipe>> = _savedRecipeList
    var currentUser: User? = null

    fun getMySavedRecipe() = viewModelScope.launch {
        repository.getMySavedRecipe().collect {
            _savedRecipeList.value = it
        }
    }

    fun getTargetUser(userId: String) = viewModelScope.launch {
        currentUser = repository.getTargetUser(userId)
    }
}