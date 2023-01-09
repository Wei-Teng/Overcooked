package com.example.overcooked.ui.viewrecipe

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.overcooked.R
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.model.User
import com.example.overcooked.adapter.IngredientFormat2Adapter
import com.example.overcooked.databinding.DialogDeleteBinding
import com.example.overcooked.databinding.DialogReportBinding
import com.example.overcooked.databinding.FragmentViewRecipeBinding
import com.example.overcooked.ui.someoneprofile.SomeoneProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewRecipeFragment : Fragment(R.layout.fragment_view_recipe) {
    private lateinit var binding: FragmentViewRecipeBinding
    private val viewModel: ViewRecipeViewModel by viewModels()

    companion object {
        fun newInstance(recipeId: String): ViewRecipeFragment {
            val args = Bundle()
            args.putString("recipeId", recipeId)
            val viewRecipeFragment = ViewRecipeFragment()
            viewRecipeFragment.arguments = args
            return viewRecipeFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentViewRecipeBinding.bind(view)
        val recipeId = requireArguments().getString("recipeId").toString()
        viewModel.getCurrentRecipe(recipeId)
        viewModel.recipeUserPair.observe(viewLifecycleOwner) { (recipe, user) ->
            setupRecipeInformation(recipe, user)
            setupIvAuthorListener(user.id)
            setupTvProfileNameListener(user.id)
            setupIbReportListener(recipe)
        }
        setupIbBackArrowListener()
    }

    private fun setupRecipeInformation(recipe: Recipe, user: User) {
        binding.title.text = recipe.title
        Glide.with(binding.ivRecipe.context)
            .load(recipe.image)
            .into(binding.ivRecipe)
        binding.tvRatingScore.text = recipe.rating.toString()
        binding.tvReviewNum.text = "(" + recipe.reviewNum.toString() + " review(s))"
        if (user.image.isNotEmpty())
            Glide.with(binding.ivAuthor.context)
                .load(user.image)
                .into(binding.ivAuthor)
        binding.tvProfileName.text = user.username
        binding.tvServesNum.text = recipe.servesNum.toString() + " Serves"
        binding.tvCookTime.text = recipe.cookTimeInMin.toString() + " min cook time"
        binding.lvIngredient.adapter =
            IngredientFormat2Adapter(requireActivity(), recipe.ingredientList.toTypedArray())
        binding.tvInstructionStep.text = recipe.instruction
        lifecycleScope.launch {
            viewModel.getCurrentUser().join()
            checkIsRateBefore(recipe, viewModel.currentUser!!.id)
            checkIsAuthor(viewModel.currentUser!!.id, recipe)
            setupIbSaveListener(recipe, viewModel.currentUser!!.id)
            setupBtFollowListener(recipe, user, viewModel.currentUser!!)
        }
    }

    private fun checkIsRateBefore(recipe: Recipe, userId: String) {
        if (recipe.raterIdList.contains(userId)) {
            binding.btRate.text = "Rated"
            binding.btRate.isEnabled = false
            binding.btRate.alpha = 0.5f
        } else
            setupBtRateListener(recipe.id)
    }

    private fun checkIsAuthor(userId: String, recipe: Recipe) {
        if (userId == recipe.authorId) {
            binding.btDelete.visibility = View.VISIBLE
            binding.btEdit.visibility = View.VISIBLE
            binding.btFollow.isEnabled = false
            binding.btFollow.visibility = View.INVISIBLE
            binding.ibSave.isEnabled = false
            binding.ibSave.visibility = View.INVISIBLE
            setupBtDeleteListener(recipe)
            setupBtEditListener(recipe.id)
        }
    }

    private fun setupBtDeleteListener(recipe: Recipe) {
        binding.btDelete.setOnClickListener {
            val customDialogBinding = DialogDeleteBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(activity)
                .setView(customDialogBinding.root)
            val alertDialog = builder.show()
            customDialogBinding.apply {
                dialogIbCancel.setOnClickListener {
                    alertDialog.dismiss()
                }
                dialogBtYes.setOnClickListener {
                    alertDialog.dismiss()
                    lifecycleScope.launch {
                        binding.btDelete.visibility = View.INVISIBLE
                        binding.pbLoading2.visibility = View.VISIBLE
                        viewModel.deleteRecipe(recipe).join()
                        binding.pbLoading2.visibility = View.INVISIBLE
                        binding.btDelete.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Delete successfully!!!", Toast.LENGTH_SHORT)
                            .show()
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
    }

    private fun setupBtEditListener(recipeId: String) {
        binding.btEdit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(R.id.fragmentContainerView, EditRecipeFragment.newInstance(recipeId))
                .addToBackStack(null).commit()
        }
    }

    private fun setupIbSaveListener(recipe: Recipe, userId: String) {
        if (recipe.savedUserIdList.contains(userId)) {
            binding.ibSave.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.green))
            binding.ibSave.setOnClickListener {
                lifecycleScope.launch {
                    binding.ibSave.visibility = View.INVISIBLE
                    binding.pbLoading3.visibility = View.VISIBLE
                    viewModel.unsaveRecipe(recipe).join()
                    viewModel.getCurrentRecipe(recipe.id).join()
                    Toast.makeText(requireContext(), "Recipe unsaved", Toast.LENGTH_SHORT).show()
                    binding.ibSave.visibility = View.VISIBLE
                    binding.pbLoading3.visibility = View.INVISIBLE
                    binding.ibSave.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.white))
                }
            }
        } else {
            binding.ibSave.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.white))
            binding.ibSave.setOnClickListener {
                lifecycleScope.launch {
                    binding.ibSave.visibility = View.INVISIBLE
                    binding.pbLoading3.visibility = View.VISIBLE
                    viewModel.saveRecipe(recipe).join()
                    viewModel.getCurrentRecipe(recipe.id).join()
                    Toast.makeText(requireContext(), "Recipe saved!!!", Toast.LENGTH_SHORT).show()
                    binding.ibSave.visibility = View.VISIBLE
                    binding.pbLoading3.visibility = View.INVISIBLE
                    binding.ibSave.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.green))
                }
            }
        }
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupIvAuthorListener(userId: String) {
        binding.ivAuthor.setOnClickListener {
            navigateToSomeoneProfilePage(userId)
        }
    }

    private fun setupTvProfileNameListener(userId: String) {
        binding.tvProfileName.setOnClickListener {
            navigateToSomeoneProfilePage(userId)
        }
    }

    private fun navigateToSomeoneProfilePage(userId: String) {
        requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
            .replace(R.id.fragmentContainerView, SomeoneProfileFragment.newInstance(userId))
            .addToBackStack(null).commit()
    }

    private fun setupBtFollowListener(recipe: Recipe, recipeUser: User, currentUser: User) =
        if (!currentUser.followingUserIdList.contains(recipeUser.id)) {
            binding.btFollow.setOnClickListener {
                lifecycleScope.launch {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btFollow.visibility = View.INVISIBLE
                    viewModel.followSomeoneProfile().join()
                    viewModel.getCurrentRecipe(recipe.id).join()
                    binding.pbLoading.visibility = View.INVISIBLE
                    binding.btFollow.visibility = View.VISIBLE
                    binding.btFollow.text = "Unfollow"
                    binding.btFollow.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                }
            }
        } else {
            binding.btFollow.text = "Unfollow"
            binding.btFollow.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
            binding.btFollow.setOnClickListener {
                lifecycleScope.launch {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btFollow.visibility = View.INVISIBLE
                    viewModel.unfollowSomeoneProfile().join()
                    viewModel.getCurrentRecipe(recipe.id).join()
                    binding.pbLoading.visibility = View.INVISIBLE
                    binding.btFollow.visibility = View.VISIBLE
                    binding.btFollow.text = "Follow"
                    binding.btFollow.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.green))
                }
            }
        }

    private fun setupBtRateListener(recipeId: String) {
        binding.btRate.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(R.id.fragmentContainerView, RatingFragment.newInstance(recipeId))
                .addToBackStack(null).commit()
        }
    }

    private fun setupIbReportListener(recipe: Recipe) {
        binding.ibReport.setOnClickListener {
            val customDialogBinding = DialogReportBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(activity)
                .setView(customDialogBinding.root)
            val alertDialog = builder.show()
            customDialogBinding.apply {
                dialogIbCancel.setOnClickListener {
                    alertDialog.dismiss()
                }
                dialogBtSend.setOnClickListener {
                    lifecycleScope.launch {
                        val reason = etReason.text.toString().trim()
                        if (reason.isNotEmpty()) {
                            pbLoading.visibility = View.VISIBLE
                            dialogBtSend.visibility = View.INVISIBLE
                            viewModel.launchReport(recipe, reason).join()
                            dialogBtSend.visibility = View.VISIBLE
                            pbLoading.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                "Report submitted!",
                                Toast.LENGTH_SHORT
                            ).show()
                            alertDialog.dismiss()
                        } else
                            Toast.makeText(
                                requireContext(),
                                "Please tell us the reason to report",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
        }
    }
}