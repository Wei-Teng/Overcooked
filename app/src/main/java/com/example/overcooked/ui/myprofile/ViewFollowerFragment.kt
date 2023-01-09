package com.example.overcooked.ui.myprofile

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.overcooked.R
import com.example.overcooked.adapter.CreatorAdapter
import com.example.overcooked.data.model.User
import com.example.overcooked.databinding.FragmentViewFollowerBinding
import com.example.overcooked.ui.someoneprofile.SomeoneProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewFollowerFragment : Fragment(R.layout.fragment_view_follower) {
    private lateinit var binding: FragmentViewFollowerBinding
    val viewModel: MyProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentViewFollowerBinding.bind(view)
        viewModel.getFollowerList()
        setupIbBackArrowListener()
        viewModel.followerList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.tvNoAnyFollower.visibility = View.INVISIBLE
            val followerAdapter = CreatorAdapter(requireContext(), it)
            binding.lvFollower.adapter = followerAdapter
            setupSvFollowerQueryTextListener(followerAdapter)
            setupLvFollowerListener(followerAdapter)
        }
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupSvFollowerQueryTextListener(followerAdapter: CreatorAdapter) {
        binding.svFollower.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.svFollower.clearFocus()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                followerAdapter.filter().filter(p0)
                return false
            }
        })
    }

    private fun setupLvFollowerListener(followerAdapter: CreatorAdapter) {
        binding.lvFollower.setOnItemClickListener { _, _, i, _ ->
            val targetUser = followerAdapter.getItem(i) as User
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