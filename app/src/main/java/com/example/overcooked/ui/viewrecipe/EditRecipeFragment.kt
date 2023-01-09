package com.example.overcooked.ui.viewrecipe

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.overcooked.data.model.Ingredient
import com.example.overcooked.R
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.adapter.IngredientFormat1Adapter
import com.example.overcooked.databinding.FragmentEditRecipeBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class EditRecipeFragment : Fragment(R.layout.fragment_edit_recipe) {
    private lateinit var binding: FragmentEditRecipeBinding
    private var imageUri: Uri? = null
    private val ingredientList = arrayListOf<Ingredient>()
    private val viewModel: ViewRecipeViewModel by viewModels()

    companion object {
        fun newInstance(recipeId: String): EditRecipeFragment {
            val args = Bundle()
            args.putString("recipeId", recipeId)
            val editRecipeFragment = EditRecipeFragment()
            editRecipeFragment.arguments = args
            return editRecipeFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditRecipeBinding.bind(view)
        val recipeId = requireArguments().getString("recipeId").toString()
        viewModel.getCurrentRecipe(recipeId)
        setupIbBackArrowListener()
        viewModel.recipeUserPair.observe(viewLifecycleOwner) { (recipe, _) ->
            binding.etTitle.hint = recipe.title
            binding.etServesNum.hint = recipe.servesNum.toString()
            binding.etCookTimeMin.hint = recipe.cookTimeInMin.toString()
            binding.etInstruction.hint = recipe.instruction
            setupBtEditRecipeListener(recipe)
        }
        val ingredientAdapter =
            IngredientFormat1Adapter(requireActivity(), ingredientList)
        binding.lvIngredient.adapter = ingredientAdapter
        setupBtAddIngredientListener(ingredientAdapter)
        setupIbRemoveIngredientListener(ingredientAdapter)
        binding.ivRecipe.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop()
                .createIntent { intent ->
                    startForRecipeImageResult.launch(intent)
                }
        }
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

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupBtAddIngredientListener(ingredientAdapter: IngredientFormat1Adapter) {
        binding.btAddIngredient.setOnClickListener {
            val ingredientName = binding.etIngredientName.text.toString().trim()
            val ingredientMass = binding.etIngredientMass.text.toString().trim().toDouble()
            if (ingredientName.isNotEmpty() && ingredientMass.isFinite()) {
                ingredientList.add(Ingredient(ingredientName, ingredientMass))
                ingredientAdapter.notifyDataSetChanged()
                binding.etIngredientName.text.clear()
                binding.etIngredientMass.text.clear()
                binding.tvEmptyIngredients.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupIbRemoveIngredientListener(ingredientAdapter: IngredientFormat1Adapter) {
        binding.lvIngredient.setOnItemClickListener { adapterView, _, i, _ ->
            adapterView.findViewById<ImageButton>(R.id.ibRemoveIngredient).setOnClickListener {
                ingredientList.removeAt(i)
                ingredientAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupBtEditRecipeListener(recipe: Recipe) {
        binding.btEditRecipe.setOnClickListener {
            if (binding.etTitle.text.toString().trim().isNotEmpty()
                && binding.etServesNum.text.toString().trim().isNotEmpty()
                && binding.etCookTimeMin.text.toString().trim().isNotEmpty()
                && binding.etInstruction.text.toString().trim().isNotEmpty()
                && imageUri != null
                && ingredientList.size > 0
            ) {
                val title = binding.etTitle.text.toString().trim()
                val servesNum = binding.etServesNum.text.toString().trim().toInt()
                val cookTime = (binding.etCookTimeMin.text.toString().trim()
                    .toDouble() * 100).roundToInt() / 100.0
                val instructions = binding.etInstruction.text.toString().trim()
                updateRecipe(recipe, title, servesNum, cookTime, instructions)
            } else
                Toast.makeText(requireContext(), "Must fill in every fields", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun updateRecipe(
        recipe: Recipe,
        title: String,
        servesNum: Int,
        cookTime: Double,
        instructions: String
    ) {
        lifecycleScope.launch {
            binding.pbLoading.visibility = View.VISIBLE
            binding.btEditRecipe.visibility = View.INVISIBLE
            val recipeDetails: HashMap<String, Any> = hashMapOf(
                "image" to imageUri!!.toString(),
                "title" to title,
                "ingredientList" to ingredientList,
                "instruction" to instructions,
                "servesNum" to servesNum,
                "cookTimeInMin" to cookTime,
            )
            viewModel.editRecipe(recipe, recipeDetails).join()
            viewModel.getCurrentRecipe(recipe.id)
            binding.pbLoading.visibility = View.INVISIBLE
            binding.btEditRecipe.visibility = View.VISIBLE
            Toast.makeText(
                requireContext(),
                "Updated successfully",
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().onBackPressed()
        }
    }
}