package com.example.overcooked.ui.someoneprofile

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
class SomeoneProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    private val _recipeList = MutableLiveData<List<Recipe>>()
    val user: LiveData<User> = _user
    val recipeList: LiveData<List<Recipe>> = _recipeList
    var currentUser: User? = null

    fun getTargetUser(userId: String) = viewModelScope.launch {
        val userData = repository.getTargetUser(userId)
        _user.value = userData
    }

    fun getCurrentUser() = viewModelScope.launch {
        currentUser = repository.getCurrentUser()
    }

    fun getUserRecipe(userId: String) = viewModelScope.launch {
        repository.getRecipe(userId).collect {
            _recipeList.value = it
        }
    }

    fun followSomeoneProfile() = viewModelScope.launch {
        repository.followSomeoneProfile(repository.getCurrentUser(), user.value!!)
    }

    fun unfollowSomeoneProfile() = viewModelScope.launch {
        repository.unfollowSomeoneProfile(
            repository.getCurrentUser(),
            user.value!!
        )
    }
}