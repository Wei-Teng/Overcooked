package com.example.overcooked.ui.myprofile

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
import com.example.overcooked.R
import com.example.overcooked.databinding.FragmentEditProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private lateinit var binding: FragmentEditProfileBinding
    private var imageUri: Uri? = null
    private val viewModel: MyProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        setupIbBackArrowListener()
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user.image.isNotEmpty())
                Glide.with(binding.ivProfile.context)
                    .load(user.image)
                    .into(binding.ivProfile)
            binding.etProfileName.hint = user.username
            binding.etBio.hint = user.bio
        }
        binding.ivProfile.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        setupBtSaveListener()
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    imageUri = fileUri
                    Glide.with(binding.ivProfile.context)
                        .load(imageUri)
                        .into(binding.ivProfile)
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

    private fun setupBtSaveListener() {
        binding.btSave.setOnClickListener {
            val newName = binding.etProfileName.text.toString().trim()
            val newBio = binding.etBio.text.toString().trim()
            if (newName.isNotEmpty() || newBio.isNotEmpty() || imageUri != null) {
                lifecycleScope.launch {
                    binding.btSave.visibility = View.INVISIBLE
                    binding.pbLoading.visibility = View.VISIBLE
                    if (newName.isNotEmpty())
                        viewModel.uploadUsername(newName).join()
                    if (newBio.isNotEmpty())
                        viewModel.uploadBio(newBio).join()
                    if (imageUri != null)
                        viewModel.uploadUserProfileImage(imageUri!!).join()
                    viewModel.getCurrentUser().join()
                    Toast.makeText(
                        requireContext(),
                        "Info updated successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    requireActivity().onBackPressed()
                    binding.btSave.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.INVISIBLE
                }
            } else
                Toast.makeText(
                    requireContext(),
                    "Please fill up at least one field",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }
}