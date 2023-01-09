package com.example.overcooked.ui.viewrecipe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.overcooked.R
import com.example.overcooked.databinding.FragmentRatingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RatingFragment : Fragment(R.layout.fragment_rating) {
    private lateinit var binding: FragmentRatingBinding
    private val viewModel: ViewRecipeViewModel by viewModels()

    companion object {
        fun newInstance(recipeId: String): RatingFragment {
            val args = Bundle()
            args.putString("recipeId", recipeId)
            val ratingFragment = RatingFragment()
            ratingFragment.arguments = args
            return ratingFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRatingBinding.bind(view)
        setupRatingBarListener()
        val recipeId = requireArguments().getString("recipeId").toString()
        setupBtSubmitListener(recipeId)
        setupIbBackArrowListener()
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRatingBarListener() {
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            when (rating) {
                1f -> binding.tvReview.text = "Very Dissatisfied!"
                2f -> binding.tvReview.text = "Dissatisfied"
                3f -> binding.tvReview.text = "Moderate"
                4f -> binding.tvReview.text = "Satisfied"
                5f -> binding.tvReview.text = "Excellent!"
                else -> binding.tvReview.text = "No Rating"
            }
            binding.tvReview.visibility = View.VISIBLE
        }
    }

    private fun setupBtSubmitListener(recipeId: String) {
        binding.btSubmit.setOnClickListener {
            if (binding.ratingBar.rating.equals(0f))
                Toast.makeText(
                    requireContext(),
                    "Please select at least one star",
                    Toast.LENGTH_SHORT
                ).show()
            else {
                lifecycleScope.launch {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btSubmit.visibility = View.INVISIBLE
                    viewModel.rateRecipePost(recipeId, binding.ratingBar.rating).join()
                    viewModel.getCurrentRecipe(recipeId)
                    binding.pbLoading.visibility = View.INVISIBLE
                    binding.btSubmit.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Rate successfully!!", Toast.LENGTH_SHORT)
                        .show()
                    requireActivity().onBackPressed()
                }
            }
        }
    }
}