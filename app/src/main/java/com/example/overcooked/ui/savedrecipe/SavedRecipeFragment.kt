package com.example.overcooked.ui.savedrecipe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.overcooked.R
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.adapter.RecipeFormat1Adapter
import com.example.overcooked.data.model.User
import com.example.overcooked.databinding.FragmentSavedRecipeBinding
import com.example.overcooked.ui.viewrecipe.ViewRecipeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedRecipeFragment : Fragment(R.layout.fragment_saved_recipe) {
    private lateinit var binding: FragmentSavedRecipeBinding
    private val viewModel: SavedRecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedRecipeBinding.bind(view)
        viewModel.getMySavedRecipe()
        viewModel.savedRecipeList.observe(viewLifecycleOwner) { recipeList ->
            if (recipeList.isNotEmpty())
                binding.tvNoSavedRecipeYet.visibility = View.INVISIBLE
            lifecycleScope.launch {
                val userList = arrayListOf<User>()
                for (recipe in recipeList) {
                    viewModel.getTargetUser(recipe.authorId).join()
                    userList.add(viewModel.currentUser!!)
                }
                val recipeAdapter = RecipeFormat1Adapter(requireActivity(), recipeList, userList)
                binding.lvSavedRecipe.adapter = recipeAdapter
                setupLvSavedRecipeListener(recipeAdapter)
            }
        }
    }

    private fun setupLvSavedRecipeListener(recipeAdapter: RecipeFormat1Adapter) {
        binding.lvSavedRecipe.setOnItemClickListener { _, _, i, _ ->
            val targetRecipe = recipeAdapter.getItem(i) as Recipe
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(
                    R.id.fragmentContainerView,
                    ViewRecipeFragment.newInstance(targetRecipe.id)
                )
                .addToBackStack(null).commit()
        }
    }
}