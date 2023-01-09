package com.example.overcooked.ui.home

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.overcooked.R
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.adapter.RecipeFormat1Adapter
import com.example.overcooked.data.model.User
import com.example.overcooked.databinding.FragmentHomeBinding
import com.example.overcooked.ui.viewrecipe.ViewRecipeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        viewModel.getRecommendedRecipe()
        viewModel.recipesLiveData.observe(viewLifecycleOwner) { recipeList ->
            if (recipeList.isNotEmpty())
                binding.tvNoRecommendedRecipeYet.visibility = View.INVISIBLE
            lifecycleScope.launch {
                val userList = arrayListOf<User>()
                for (recipe in recipeList) {
                    viewModel.getTargetUser(recipe.authorId).join()
                    userList.add(viewModel.targetUser!!)
                }
                val recommendedRecipeAdapter =
                    RecipeFormat1Adapter(requireActivity(), recipeList, userList)
                binding.lvRecommendedRecipe.adapter = recommendedRecipeAdapter
                setupSvRecipeQueryTextListener(recommendedRecipeAdapter)
                setupLvRecommendedRecipeListener(recommendedRecipeAdapter)
            }
        }
    }

    private fun setupSvRecipeQueryTextListener(recommendedRecipeAdapter: RecipeFormat1Adapter) {
        binding.svRecipe.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.svRecipe.clearFocus()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                recommendedRecipeAdapter.filter().filter(p0)
                return false
            }
        })
    }

    private fun setupLvRecommendedRecipeListener(recommendedRecipeAdapter: RecipeFormat1Adapter) {
        binding.lvRecommendedRecipe.setOnItemClickListener { _, _, i, _ ->
            val recipe = recommendedRecipeAdapter.getItem(i) as Recipe
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(R.id.fragmentContainerView, ViewRecipeFragment.newInstance(recipe.id))
                .addToBackStack(null).commit()
        }
    }
}