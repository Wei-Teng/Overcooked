package com.example.overcooked.ui.addrecipe

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.overcooked.data.model.Ingredient
import com.example.overcooked.R
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.adapter.IngredientFormat1Adapter
import com.example.overcooked.databinding.FragmentAddRecipeBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class AddRecipeFragment : Fragment(R.layout.fragment_add_recipe) {
    private lateinit var binding: FragmentAddRecipeBinding
    private var imageUri: Uri? = null
    private val ingredientList = arrayListOf<Ingredient>()
    private val viewModel: AddRecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddRecipeBinding.bind(view)
        val ingredientAdapter =
            IngredientFormat1Adapter(requireActivity(), ingredientList)
        binding.lvIngredient.adapter = ingredientAdapter
        setupBtAddIngredientListener(ingredientAdapter)
        binding.ivRecipe.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop()
                .createIntent { intent ->
                    startForRecipeImageResult.launch(intent)
                }
        }
        setupBtPostRecipeListener()
    }

    private val startForRecipeImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    imageUri = fileUri
                    Glide.with(binding.ivRecipe.context)
                        .load(imageUri)
                        .into(binding.ivRecipe)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun setupBtAddIngredientListener(ingredientAdapter: IngredientFormat1Adapter) {
        binding.btAddIngredient.setOnClickListener {
            if (binding.etIngredientName.text.toString()
                    .isNotBlank() && binding.etIngredientMass.text.toString().isNotBlank()
            ) {
                val ingredientName = binding.etIngredientName.text.toString().trim()
                val ingredientMass = binding.etIngredientMass.text.toString().trim().toDouble()
                if (ingredientName.isNotEmpty() && ingredientMass.isFinite()) {
                    ingredientList.add(Ingredient(ingredientName, ingredientMass))
                    ingredientAdapter.notifyDataSetChanged()
                }
                binding.etIngredientName.text.clear()
                binding.etIngredientMass.text.clear()
                binding.tvEmptyIngredients.visibility = View.INVISIBLE
            } else
                Toast.makeText(
                    requireContext(),
                    "Please fill up all ingredients field",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun setupBtPostRecipeListener() {
        binding.btPostRecipe.setOnClickListener {
            if (binding.etTitle.text.toString().trim().isNotEmpty()
                && binding.etServesNum.text.toString().trim().isNotEmpty()
                && binding.etCookTimeMin.text.toString().trim().isNotEmpty()
                && binding.etInstruction.text.toString().trim().isNotEmpty()
                && ingredientList.size > 0
                && imageUri != null
            ) {
                val title = binding.etTitle.text.toString().trim()
                val servesNum = binding.etServesNum.text.toString().trim().toInt()
                val cookTime = (binding.etCookTimeMin.text.toString().trim()
                    .toDouble() * 100).roundToInt() / 100.0
                val instructions = binding.etInstruction.text.toString().trim()
                storeRecipe(
                    Recipe(
                        imageUri!!.toString(),
                        title,
                        ingredientList,
                        instructions,
                        servesNum,
                        cookTime
                    )
                )
            } else
                Toast.makeText(requireContext(), "Must fill in every fields", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun storeRecipe(recipe: Recipe) {
        lifecycleScope.launch {
            binding.pbLoading.visibility = View.VISIBLE
            binding.btPostRecipe.visibility = View.INVISIBLE
            viewModel.postRecipe(recipe).join()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, AddRecipeFragment())
                .commit()
            Toast.makeText(
                requireContext(),
                "Post successfully",
                Toast.LENGTH_SHORT
            ).show()
            binding.pbLoading.visibility = View.INVISIBLE
            binding.btPostRecipe.visibility = View.VISIBLE
        }
    }
}