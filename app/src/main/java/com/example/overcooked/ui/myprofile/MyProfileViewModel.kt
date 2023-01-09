package com.example.overcooked.ui.myprofile

import android.net.Uri
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
class MyProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    private val _recipesLiveData = MutableLiveData<List<Recipe>>()
    val recipesLiveData: LiveData<List<Recipe>> = _recipesLiveData
    private val _followingList = MutableLiveData<List<User>>()
    private val _followerList = MutableLiveData<List<User>>()
    val followingList: LiveData<List<User>> = _followingList
    val followerList: LiveData<List<User>> = _followerList

    fun getCurrentUser() = viewModelScope.launch {
        _user.value = repository.getCurrentUser()
    }

    fun uploadUserProfileImage(uri: Uri) = viewModelScope.launch {
        repository.uploadImage(uri)
        _user.value = repository.getCurrentUser()
    }

    fun uploadUsername(newName: String) = viewModelScope.launch {
        repository.uploadName(newName)
    }

    fun uploadBio(newBio: String) = viewModelScope.launch {
        repository.uploadBio(newBio)
    }

    fun getRecipe() = viewModelScope.launch {
        repository.getRecipe(user.value!!.id).collect { response ->
            _recipesLiveData.value = response
        }
    }

    fun getFollowingList() = viewModelScope.launch {
        repository.getFollowingList().collect { response ->
            _followingList.value = response
        }
    }

    fun getFollowerList() = viewModelScope.launch {
        repository.getFollowerList().collect { response ->
            _followerList.value = response
        }
    }
}