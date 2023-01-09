package com.example.overcooked.ui.creator

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.overcooked.R
import com.example.overcooked.data.model.User
import com.example.overcooked.adapter.CreatorAdapter
import com.example.overcooked.databinding.FragmentCreatorBinding
import com.example.overcooked.ui.someoneprofile.SomeoneProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatorFragment : Fragment(R.layout.fragment_creator) {

    private lateinit var binding: FragmentCreatorBinding
    private val viewModel: CreatorViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreatorBinding.bind(view)
        setupIbBackArrowListener()
        viewModel.creators.observe(viewLifecycleOwner) {
            val creatorAdapter = CreatorAdapter(requireContext(), it)
            binding.gvCreator.adapter = creatorAdapter
            setupSvCreatorQueryTextListener(creatorAdapter)
            setupGridViewListener(creatorAdapter)
        }
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupSvCreatorQueryTextListener(creatorAdapter: CreatorAdapter) {
        binding.svCreator.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.svCreator.clearFocus()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                creatorAdapter.filter().filter(p0)
                return false
            }
        })
    }

    private fun setupGridViewListener(creatorAdapter: CreatorAdapter) {
        binding.gvCreator.setOnItemClickListener { _, _, i, _ ->
            val targetUser = creatorAdapter.getItem(i) as User
            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
                .replace(
                    R.id.fragmentContainerView,
                    SomeoneProfileFragment.newInstance(targetUser.id)
                )
                .addToBackStack(null).commit()
        }
    }
}