package com.example.overcooked.ui.myprofile

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.overcooked.R
import com.example.overcooked.adapter.CreatorAdapter
import com.example.overcooked.data.model.User
import com.example.overcooked.databinding.FragmentViewFollowingBinding
import com.example.overcooked.ui.someoneprofile.SomeoneProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewFollowingFragment : Fragment(R.layout.fragment_view_following) {
    private lateinit var binding: FragmentViewFollowingBinding
    val viewModel: MyProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentViewFollowingBinding.bind(view)
        viewModel.getFollowingList()
        setupIbBackArrowListener()
        viewModel.followingList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.tvNoAnyFollowing.visibility = View.INVISIBLE
            val followingAdapter = CreatorAdapter(requireContext(), it)
            binding.lvFollowing.adapter = followingAdapter
            setupSvFollowingQueryTextListener(followingAdapter)
            setupLvFollowingListener(followingAdapter)
        }
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupSvFollowingQueryTextListener(followingAdapter: CreatorAdapter) {
        binding.svFollowing.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.svFollowing.clearFocus()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                followingAdapter.filter().filter(p0)
                return false
            }
        })
    }

    private fun setupLvFollowingListener(followingAdapter: CreatorAdapter) {
        binding.lvFollowing.setOnItemClickListener { _, _, i, _ ->
            val targetUser = followingAdapter.getItem(i) as User
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