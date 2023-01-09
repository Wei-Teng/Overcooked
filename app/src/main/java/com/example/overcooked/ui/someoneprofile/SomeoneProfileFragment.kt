package com.example.overcooked.ui.someoneprofile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.overcooked.R
import com.example.overcooked.data.model.User
import com.example.overcooked.adapter.RecipeFormat2Adapter
import com.example.overcooked.databinding.FragmentSomeoneProfileBinding
import com.example.overcooked.ui.myprofile.MyProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SomeoneProfileFragment : Fragment(R.layout.fragment_someone_profile) {
    private lateinit var binding: FragmentSomeoneProfileBinding
    private val viewModel: SomeoneProfileViewModel by viewModels()
    private val viewModel2: MyProfileViewModel by viewModels()

    companion object {
        fun newInstance(userId: String): SomeoneProfileFragment {
            val args = Bundle()
            args.putString("userId", userId)
            val someoneProfileFragment = SomeoneProfileFragment()
            someoneProfileFragment.arguments = args
            return someoneProfileFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSomeoneProfileBinding.bind(view)
        val userId = requireArguments().getString("userId").toString()
        viewModel.getTargetUser(userId)
        viewModel.getUserRecipe(userId)
        setupIbBackArrowListener()
        viewModel.user.observe(viewLifecycleOwner) { user ->
            setupUserProfileInformation(user)
            setupBtFollowListener(user)
        }
        viewModel.recipeList.observe(viewLifecycleOwner) { recipeList ->
            if (recipeList.isNotEmpty())
                binding.tvNoRecipeYet.visibility = View.INVISIBLE
            binding.rvSomeoneRecipe.layoutManager = LinearLayoutManager(requireContext())
            val recyclerViewState = binding.rvSomeoneRecipe.layoutManager?.onSaveInstanceState()
            binding.rvSomeoneRecipe.adapter =
                RecipeFormat2Adapter(
                    requireContext(),
                    recipeList,
                    requireActivity()
                )
            binding.rvSomeoneRecipe.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }

    private fun setupUserProfileInformation(user: User) {
        if (user.image.isNotEmpty())
            Glide.with(binding.ivProfile.context)
                .load(user.image)
                .into(binding.ivProfile)
        binding.tvProfileName.text = user.username
        binding.tvBio.text = user.bio
        binding.tvFollowerNum.text = user.followerNum.toString()
        binding.tvFollowingNum.text = user.followingNum.toString()
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupBtFollowListener(user: User) {
        lifecycleScope.launch {
            viewModel.getCurrentUser().join()
            val currentUser = viewModel.currentUser
            if (!currentUser!!.followingUserIdList.contains(user.id)) {
                binding.btFollow.setOnClickListener {
                    lifecycleScope.launch {
                        binding.pbLoading.visibility = View.VISIBLE
                        binding.btFollow.visibility = View.INVISIBLE
                        viewModel.followSomeoneProfile().join()
                        viewModel.getTargetUser(user.id).join()
                        viewModel2.getCurrentUser().join()
                        binding.pbLoading.visibility = View.INVISIBLE
                        binding.btFollow.visibility = View.VISIBLE
                        binding.btFollow.text = "Unfollow"
                    }
                }
            } else {
                binding.btFollow.text = "Unfollow"
                binding.btFollow.setOnClickListener {
                    lifecycleScope.launch {
                        binding.pbLoading.visibility = View.VISIBLE
                        binding.btFollow.visibility = View.INVISIBLE
                        viewModel.unfollowSomeoneProfile().join()
                        viewModel.getTargetUser(user.id).join()
                        viewModel2.getCurrentUser().join()
                        binding.pbLoading.visibility = View.INVISIBLE
                        binding.btFollow.visibility = View.VISIBLE
                        binding.btFollow.text = "Follow"
                    }
                }
            }
        }
    }
}