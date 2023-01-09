package com.example.overcooked.ui.myprofile

import android.app.AlertDialog
import android.content.Intent
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
import com.example.overcooked.databinding.DialogLogoutBinding
import com.example.overcooked.databinding.FragmentMyProfileBinding
import com.example.overcooked.entry.LoginActivity
import com.example.overcooked.ui.creator.CreatorFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProfileFragment : Fragment(R.layout.fragment_my_profile) {
    private lateinit var binding: FragmentMyProfileBinding
    private val viewModel: MyProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyProfileBinding.bind(view)
        setupIbLogoutListener()
        setupBtEditProfileListener()
        setupBtFollowOtherListener()
        lifecycleScope.launch {
            viewModel.getCurrentUser().join()
            viewModel.getRecipe().join()
        }
        //todo id list
        viewModel.user.observe(viewLifecycleOwner) { user ->
            setupUserProfileInformation(user)
            if (user.followerUserIdList.isNotEmpty())
                setupTvFollowerNumListener()
            if (user.followingUserIdList.isNotEmpty())
                setupTvFollowingNumListener()
        }
        viewModel.recipesLiveData.observe(viewLifecycleOwner) { recipeList ->
            if (recipeList.isNotEmpty())
                binding.tvNoRecipeYet.visibility = View.INVISIBLE
            binding.rvMyRecipe.layoutManager = LinearLayoutManager(requireContext())
            val recyclerViewState = binding.rvMyRecipe.layoutManager?.onSaveInstanceState()
            binding.rvMyRecipe.adapter =
                RecipeFormat2Adapter(
                    requireContext(),
                    recipeList,
                    requireActivity()
                )
            binding.rvMyRecipe.layoutManager?.onRestoreInstanceState(recyclerViewState)
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

    private fun setupIbLogoutListener() {
        binding.ibLogout.setOnClickListener {
            val customDialogBinding = DialogLogoutBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(activity)
                .setView(customDialogBinding.root)
            val alertDialog = builder.show()
            customDialogBinding.apply {
                dialogIbCancel.setOnClickListener {
                    alertDialog.dismiss()
                }
                dialogBtYes.setOnClickListener {
                    alertDialog.dismiss()
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun setupBtEditProfileListener() {
        binding.btEditProfile.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(R.id.fragmentContainerView, EditProfileFragment())
                .addToBackStack(null).commit()
        }
    }

    private fun setupBtFollowOtherListener() {
        binding.btFollowOther.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(R.id.fragmentContainerView, CreatorFragment())
                .addToBackStack(null).commit()
        }
    }

    private fun setupTvFollowerNumListener() {
        binding.tvFollowerNum.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(
                    R.id.fragmentContainerView,
                    ViewFollowerFragment()
                )
                .addToBackStack(null).commit()
        }
    }

    private fun setupTvFollowingNumListener() {
        binding.tvFollowingNum.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(
                    R.id.fragmentContainerView,
                    ViewFollowingFragment()
                )
                .addToBackStack(null).commit()
        }
    }
}