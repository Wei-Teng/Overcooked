package com.example.overcooked.ui.creator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.overcooked.data.model.User
import com.example.overcooked.data.repositories.creator.CreatorRepository
import com.example.overcooked.data.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatorViewModel @Inject constructor(
    private val creatorRepository: CreatorRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _creatorsLiveData = MutableLiveData<List<User>>()
    val creators: LiveData<List<User>> = _creatorsLiveData

    init {
        getPopularCreatorList()
    }

    private fun getPopularCreatorList() = viewModelScope.launch {
        creatorRepository.getPopularCreatorList().collect {
            val arrayList = ArrayList<User>(it)
            for (creator in it) {
                val user = profileRepository.getCurrentUser()
                if (creator.id == user.id)
                    arrayList.remove(creator)
            }
            _creatorsLiveData.value = arrayList.toList()
        }
    }
}