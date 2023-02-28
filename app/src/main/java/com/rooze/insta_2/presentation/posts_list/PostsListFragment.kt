package com.rooze.insta_2.presentation.posts_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.FragmentPostsListBinding
import com.rooze.insta_2.presentation.MainViewModel
import com.rooze.insta_2.presentation.common.collectWhenActive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostsListFragment : Fragment(), PostsListListener {

    companion object {
        private const val TAG = "PostsListFragment"
    }

    private val viewModel: PostsViewModel by viewModels()

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostsListBinding.inflate(inflater, container, false)

        binding.layoutManager = LinearLayoutManager(context)
        binding.adapter = PostsAdapter(this)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.message.collectWhenActive(viewLifecycleOwner) { message ->
            context?.applicationContext?.let {
                Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
            }
        }
        mainViewModel.reloadMap.collectWhenActive(viewLifecycleOwner) { map ->
            Log.i(TAG, "observeViewModel: $map")
            if (map["PostsList:posts"] == true) {
                viewModel.reloadPosts()
                mainViewModel.markReloaded("PostsList:posts")
            }
        }
    }

    override fun like(postId: String) {
        viewModel.like(postId)
    }

    override fun openComment(postId: String) {
    }

    override fun openPost(postId: String) {
        mainViewModel.openPost(postId)
    }

    override fun openAccount(accountId: String) {
        findNavController().navigate(R.id.launch_profile, bundleOf("accountId" to accountId))
    }

}