package com.example.overcooked.ui.addrecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {
    fun postRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.postRecipe(recipe)
    }
}